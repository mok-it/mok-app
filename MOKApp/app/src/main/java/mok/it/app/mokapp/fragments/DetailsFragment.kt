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
    private lateinit var joinButton: Button

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

        initLayout()
        getMembers()
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
        }

        // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
    }

    fun getMembers(){
        
    }
}