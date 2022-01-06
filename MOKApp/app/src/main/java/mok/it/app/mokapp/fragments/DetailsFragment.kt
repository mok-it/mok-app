package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import kotlin.coroutines.CoroutineContext

class DetailsFragment(badgeId: String) : Fragment(){
    val badgeId = badgeId
    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "/projects";
    val userCollectionPath: String = "/users";
    val TAG = "DetailsFragment"
    lateinit var model: Project
    lateinit var memberUsers: ArrayList<User>
    private lateinit var avatarImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val docRef = firestore.collection(projectCollectionPath).document(badgeId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    model = document.toObject(Project::class.java)!!
                    //getMembers(model.members)
                    //Log.d(TAG, "Model data: ${model}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
    }

    suspend fun initData(members: List<String>?): Unit = coroutineScope {
        val one = async { getMembers(members) }
        one.await()
        val two = async { initLayout() }
    }

    fun initLayout(){
        //recyclerView = this.requireView().findViewById(R.id.recyclerView)
        avatarImageView = this.requireView().findViewById(R.id.avatar_imagebutton) as ImageView

        /*val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser!!
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(this)
            .load(currentUser?.photoUrl)
            .apply( requestOptions.override(100, 100))
            .into(avatarImageView)*/

        // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
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

                        /*if (members.size == memberUsers.size){
                            initRecyclerView()
                        }*/
                    }
                }

        }
    }
}