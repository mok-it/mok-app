package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import java.text.SimpleDateFormat

class DetailsFragment(badgeId: String) : BaseFireFragment(), MembersAdapter.MemberClickedListener,
    BadgeAcceptMemberDialogFragment.SuccessListener {
    val badgeId = badgeId
    val projectCollectionPath: String = "/projects";
    val userCollectionPath: String = "/users";
    val TAG = "DetailsFragment"
    lateinit var memberUsers: ArrayList<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var joinButton: Button
    private lateinit var functions: FirebaseFunctions
    private var selectedMember = ""
    private lateinit var badgeName: TextView
    private lateinit var badgeDescription: TextView
    private lateinit var badgeCreator: TextView
    private lateinit var badgeDeadline: TextView
    private lateinit var badgeProgress: ProgressBar
    private lateinit var badgeIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Detail")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        functions = Firebase.functions
        getMemberIds()
        initLayout()
    }


    fun initLayout(){
        val membersBtn = this.requireView().findViewById(R.id.toggleMembersButton) as Button
        membersBtn.setOnClickListener{
            membersBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if(recyclerView.isVisible) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up, 0, 0, 0)
            recyclerView.isVisible = !recyclerView.isVisible
        }
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))

        joinButton = this.requireView().findViewById(R.id.join_button) as Button
        joinButton.setOnClickListener {
            Toast.makeText(getContext(), "Congrats, you joined!", Toast.LENGTH_SHORT).show()
            join()
        }
        //bind the badge name, description, progress bar and icon controls
        badgeName = this.requireView().findViewById(R.id.disc_name_textview)
        badgeDescription = this.requireView().findViewById(R.id.description_textview)
        badgeCreator = this.requireView().findViewById(R.id.creator_textview)
        badgeDeadline = this.requireView().findViewById(R.id.deadline_textview)
        badgeProgress = this.requireView().findViewById(R.id.progressBar)

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
                badgeProgress.setProgress((document.get("overall_progress") as Number).toInt())
                Picasso.get().load(document.get("icon") as String).into(avatar_imagebutton)
            }
        }
        // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
    }

    fun getMemberIds(){
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

    fun getMembers(members: List<String>?){
        memberUsers = ArrayList<User>()
        Log.d(TAG, "LIST: ${members}")
        members?.forEach {
            val docRef = firestore.collection(userCollectionPath).document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)!!
                        memberUsers.add(user)
                        Log.d(TAG, "MEMBERS: ${memberUsers}")

                        if (members.size == memberUsers.size){
                            initRecyclerView(MembersAdapter(memberUsers, this))
                        }
                    }
                }
        }
        initRecyclerView(MembersAdapter(memberUsers, this))
    }

    fun join(){
        val data = hashMapOf(
            "uid" to currentUser.uid,
            "badgeid" to badgeId
        )

        val userRef = firestore.collection("users").document(currentUser.uid)
        userRef.update("joinedBadges", FieldValue.arrayUnion(badgeId))

        val badgeRef = firestore.collection("projects").document(badgeId)
        badgeRef.update("members", FieldValue.arrayUnion(currentUser.uid))
            .addOnCompleteListener {
                getMemberIds()
            }
    }


    fun completed(userId: String){
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

    override fun onMemberClicked(userId: String) {
        selectedMember = userId
        val dialog = BadgeAcceptMemberDialogFragment(this)
        dialog.show(parentFragmentManager, "AcceptDialog")

    }

    override fun onSuccess() {
        completed(selectedMember)
    }
}