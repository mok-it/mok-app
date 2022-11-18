package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.currentUser
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.interfaces.UserRefreshedListener
import mok.it.app.mokapp.interfaces.UserRefresher
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.model.getIconFileName
import mok.it.app.mokapp.recyclerview.MembersAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class DetailsFragment(private val badgeId: String, private val userRefresher: UserRefresher) : BaseFireFragment(), MembersAdapter.MemberClickedListener,
    BadgeAcceptMemberDialogFragment.SuccessListener, UserRefreshedListener{

    lateinit var badgeModel: Project
    private val commentsId = "comments"
    private val TAG = "DetailsFragment"
    lateinit var memberUsers: ArrayList<User>
    lateinit var memberComments: ArrayList<Comment>
    var userIsEditor: Boolean = false
    private var selectedMemberUID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMemberIds()
        getCommentIds()
        initLayout()
    }

    private fun initLayout() {
        members_overlay_button.setOnClickListener {
            openMembersDialog()
        }
        join_button.setOnClickListener {
            join()
            userRefresher.refreshUser(this)
        }
        join_button.visibility = View.GONE
        badgeComments.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommentsFragment(badgeId), "CommentsFragment")
                .commit()
        }
        documentOnSuccess(projectCollectionPath, badgeId) { document ->
            if (document != null) {

                badgeModel = document.toObject(Project::class.java)!!

                badgeName.text = document.get("name") as String
                categoryName.text = getString(R.string.category) + ": " + document.get("category") as String
                badgeDescription.text = document.get("description") as String
                firestore.collection(userCollectionPath).document(document.get("creator") as String)
                    .get().addOnSuccessListener { creatorDoc ->
                        if (creatorDoc?.get("name") != null) {
                            badgeCreator.text = creatorDoc.get("name") as String
                        }
                    }
                val formatter = SimpleDateFormat("yyyy.MM.dd")
                badgeDeadline.text =
                    formatter.format((document.get("deadline") as Timestamp).toDate())
                //badgeProgress.progress = (document.get("overall_progress") as Number).toInt()

                val iconURL = document.get("icon") as String
                val iconFileName = getIconFileName(iconURL)
                val iconFile = File(context?.filesDir, iconFileName)
                if (iconFile.exists()){
                    Log.i(TAG, "loading badge icon " + iconFile.path)
                    val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
                    avatar_imagebutton.setImageBitmap(bitmap)
                }
                else {
                    Log.i(TAG, "downloading badge icon " + model.icon)
                    val callback = object: Callback {
                        override fun onSuccess() {
                            // save image
                            Log.i(TAG, "saving badge icon " + iconFile.path)
                            val bitmap : Bitmap = avatar_imagebutton.drawable.toBitmap()
                            var fos: FileOutputStream?
                            try {
                                fos = FileOutputStream(iconFile)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                fos.flush()
                                fos.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    Picasso.get().load(iconURL).into(avatar_imagebutton, callback)
                }

                val editors = document.get("editors") as List<String>
                if (editors.contains(userModel.uid)) {
                    userIsEditor = true
                }
                changeVisibilities()
            }
        }
        // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
    }

    private fun getMemberIds() {
        val docRef = firestore.collection(projectCollectionPath).document(badgeId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    model = document.toObject(Project::class.java)!!
                    getMembers(model.members)
                    Log.d(TAG, "Model data: ${model}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
    }

    private fun getMembers(members: List<String>?) {
        memberUsers = ArrayList()
        Log.d(TAG, "LIST: ${members}")
        members?.forEach {
            val docRef = firestore.collection(userCollectionPath).document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)!!
                        memberUsers.add(user)
                        Log.d(TAG, "MEMBERS: ${memberUsers}")

                        if (members.size == memberUsers.size) {
                            initMembers()
                        }
                    }
                }
        }
        initMembers()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCommentIds() {
        memberComments = ArrayList<Comment>()
        val collectionRef =
            firestore.collection(projectCollectionPath).document(badgeId).collection(commentsId)
        collectionRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null && collection.documents.size > 0) {
                    for (document in collection.documents) {
                        val comment = document.toObject(Comment::class.java)!!
                        memberComments.add(comment)
                    }
                    memberComments.sortByDescending { comment: Comment -> comment.time.toDate() }

                    val formatter = SimpleDateFormat("yyyy.MM.dd. hh:mm")
                    val timeString: String = formatter.format(memberComments[0].time.toDate())

                    // Search user with given uid among the members
                    var sender = "anonymous"
                    val docRef =
                        firestore.collection(userCollectionPath).document(memberComments[0].uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val user = document.toObject(User::class.java)!!
                                sender = user.name
                            }
                            badgeComments.text = getString(
                                R.string.newest_comment_text,
                                timeString,
                                sender,
                                memberComments[0].text
                            )
                        }
                } else {
                    Log.d(TAG, "No such collection")
                }
            }
    }

    /**
     * We adjust the extra member counter's text size based on the length of it
     */
    private fun initExtraMemberCounter(numOfExtraMembers: Int) {

        val textSizeResource = when ("$numOfExtraMembers".length) {
            1 -> R.dimen.profile_circle_leftover_text_size_1_digit
            2 -> R.dimen.profile_circle_leftover_text_size_2_digit
            3 -> R.dimen.profile_circle_leftover_text_size_3_digit
            else -> R.dimen.profile_circle_leftover_text_size_3_digit
        }

        val textSizeSP = resources.getDimension(textSizeResource)

        Log.d(TAG, "textSizeSP = $textSizeSP")

        members_left_number.text = "+$numOfExtraMembers"
        members_left_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSP)

    }

    private fun initMembers() {
        members_left.isVisible = false
        members_left_number.isVisible = false

        val members = listOf(member1, member2, member3)

        for (i in 0 until 3) {
            if (memberUsers.size > i) {
                Picasso.get().load(memberUsers[i].photoURL).into(members[i])
                members[i].isVisible = true
            } else {
                members[i].isVisible = false
            }
        }

        if (memberUsers.size >= 4) {
            initExtraMemberCounter(memberUsers.size - 3)
            members_left.isVisible = true
            members_left_number.isVisible = true
        }
    }

    private fun join(){
        if (userModel.joinedBadges.contains(badgeId)){
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.update("joinedBadges", FieldValue.arrayRemove(badgeId))

            val badgeRef = firestore.collection("projects").document(badgeId)
            badgeRef.update("members", FieldValue.arrayRemove(currentUser.uid))
                .addOnCompleteListener {
                    Toast.makeText(context, "Sikeresen lecsatlakoztál!", Toast.LENGTH_SHORT).show()
                    getMemberIds()
                }
        }
        else {
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.update("joinedBadges", FieldValue.arrayUnion(badgeId))

            val badgeRef = firestore.collection("projects").document(badgeId)
            badgeRef.update("members", FieldValue.arrayUnion(currentUser.uid))
                .addOnCompleteListener {
                    Toast.makeText(context, "Sikeresen csatlakoztál!", Toast.LENGTH_SHORT).show()
                    getMemberIds()
                }
        }
    }


    private fun completed(userId: String) {
        Log.d("DetailsFragment", "completed")
        Log.d("DetailsFragment", "BadgeID: " + badgeId)
        val userRef = firestore.collection("users").document(userId)
        userRef.update("joinedBadges", FieldValue.arrayRemove(badgeId)).addOnSuccessListener { Log.d("DetailsFragment", badgeId + " removed from " + userId) }.addOnFailureListener{e -> Log.d("DetailsFragment", "wtf " + e)}
        userRef.update("collectedBadges", FieldValue.arrayUnion(badgeId))
        val badgeRef = firestore.collection("projects").document(badgeId)
        badgeRef.update("members", FieldValue.arrayRemove(userId))
            .addOnCompleteListener {
                getMemberIds()
                Log.d("DetailsFragment", "removed")
            }
    }

    private fun openMembersDialog() {
        val dialog = BadgeAllMemberDialogFragment(memberUsers, this, userIsEditor)
        dialog.show(parentFragmentManager, "MembersDialog")
    }

    override fun onMemberClicked(user: User) {
        selectedMemberUID = user.uid
        val dialog = BadgeAcceptMemberDialogFragment(this, user.name)
        dialog.show(parentFragmentManager, "AcceptDialog")
    }

    override fun onSuccess() {
        completed(selectedMemberUID)
    }

    private fun changeVisibilities(){
        join_button.visibility = View.VISIBLE
        if (userModel.collectedBadges.contains(badgeModel.id))
            join_button.visibility = View.GONE
        else if (userModel.joinedBadges.contains(badgeModel.id))
            join_button.text = "Lecsatlakozás"
        else if (!userModel.joinedBadges.contains(badgeModel.id))
            join_button.text = "Csatlakozás"

        if (badgeModel.editors.contains(userModel.uid)){
            userIsEditor = true
        }
    }

    override fun userRefreshed() {
        changeVisibilities()
    }
}