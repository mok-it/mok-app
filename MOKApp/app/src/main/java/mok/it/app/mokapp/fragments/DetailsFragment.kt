package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class DetailsFragment(badgeId: String) : Fragment(), MembersAdapter.MemberClickedListener,
    BadgeAcceptMemberDialogFragment.SuccessListener {
    val badgeId = badgeId
    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "/projects";
    val userCollectionPath: String = "/users";
    val TAG = "DetailsFragment"
    lateinit var model: Project
    lateinit var memberUsers: ArrayList<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var avatarImageView: ImageView
    private lateinit var joinButton: Button
    private lateinit var functions: FirebaseFunctions
    private var selectedMember = ""


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
        avatarImageView = this.requireView().findViewById(R.id.avatar_imagebutton) as ImageView

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser!!
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(this)
            .load(currentUser?.photoUrl)
            .into(avatarImageView)

        joinButton = this.requireView().findViewById(R.id.join_button) as Button
        joinButton.setOnClickListener {
            Toast.makeText(getContext(), "Congrats, you joined!", Toast.LENGTH_SHORT).show()
            join()
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
                            initRecyclerView()
                        }
                    }
                }
        }
        initRecyclerView()
    }

    fun initRecyclerView(){
        recyclerView = this.requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = MembersAdapter(memberUsers, this)
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    fun join(){
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser!!
        currentUser.uid

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