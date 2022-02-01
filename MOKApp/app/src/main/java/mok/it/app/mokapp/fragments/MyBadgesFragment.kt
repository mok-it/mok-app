package mok.it.app.mokapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.BadgesAdapter

class MyBadgesFragment : Fragment(), BadgesAdapter.BadgeClickedListener {
    var mAuth = FirebaseAuth.getInstance()
    var currentUser = mAuth.currentUser
    lateinit var userModel: User
    private lateinit var recyclerView: RecyclerView
    lateinit var collectedBadges: ArrayList<Project>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser(currentUser!!.uid)

    }

    fun getUser(uid: String) {
        Firebase.firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userModel = document.toObject(User::class.java)!!
                    getBadges(userModel.collectedBadges)
                }
            }
    }

    fun getBadges(badges: List<String>?) {
        collectedBadges = ArrayList<Project>()
        badges?.forEach {
            val docRef = Firebase.firestore.collection("projects").document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val badge = document.toObject(Project::class.java)!!
                        collectedBadges.add(badge)
                        if (badges.size == collectedBadges.size) {
                            initRecyclerView()
                        }
                    }
                }
        }
    }

    fun initRecyclerView() {
        recyclerView = this.requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = BadgesAdapter(collectedBadges, this)
        recyclerView.layoutManager =
            GridLayoutManager(this.context, 2, LinearLayoutManager.VERTICAL, false)
    }

    override fun onBadgeClicked(badgeId: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DetailsFragment(badgeId), "DetailsFragment").commit()
    }
}