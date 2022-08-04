package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
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
import androidx.core.view.isVisible
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.currentUser
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class DetailsFragment(badgeId: String) : BaseFireFragment(), MembersAdapter.MemberClickedListener,
    BadgeAcceptMemberDialogFragment.SuccessListener {

    private val badgeId = badgeId
    private val commentsId = "comments"
    private val TAG = "DetailsFragment"
    lateinit var memberUsers: ArrayList<User>
    lateinit var memberComments: ArrayList<Comment>
    var userIsEditor: Boolean = false
    private lateinit var joinButton: Button
    private var selectedMember = ""

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

        joinButton = this.requireView().findViewById(R.id.join_button) as Button
        joinButton.setOnClickListener {
            Toast.makeText(context, "Congrats, you joined!", Toast.LENGTH_SHORT).show()
            join()
        }
        badgeComments.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommentsFragment(badgeId), "CommentsFragment")
                .commit()
        }
        documentOnSuccess(projectCollectionPath, badgeId) { document ->
            if (document != null) {
                badgeName.text = document.get("name") as String
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
                badgeProgress.progress = (document.get("overall_progress") as Number).toInt()
                Picasso.get().load(document.get("icon") as String).into(avatar_imagebutton)

                val editors = document.get("editors") as List<String>
                if (editors.contains(userModel.uid)) {
                    userIsEditor = true
                }
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

        val strlen = "$numOfExtraMembers".length

        val textSizeResource = when (strlen) {
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

    private fun join() {
        val userRef = firestore.collection("users").document(currentUser.uid)
        userRef.update("joinedBadges", FieldValue.arrayUnion(badgeId))

        val badgeRef = firestore.collection("projects").document(badgeId)
        badgeRef.update("members", FieldValue.arrayUnion(currentUser.uid))
            .addOnCompleteListener {
                getMemberIds()
            }
    }


    private fun completed(userId: String) {
        Log.d("DetailsFragment", "completed")
        val userRef = firestore.collection("users").document(userId)
        userRef.update("joinedBadges", FieldValue.arrayRemove(badgeId))
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

    override fun onMemberClicked(userId: String) {
        selectedMember = userId
        val dialog = BadgeAcceptMemberDialogFragment(this)
        dialog.show(parentFragmentManager, "AcceptDialog")
    }

    override fun onSuccess() {
        completed(selectedMember)
    }
}