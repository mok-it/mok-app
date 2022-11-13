package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.BadgeCategoriesAdapter
import mok.it.app.mokapp.recyclerview.BadgesAdapter

class MyBadgesFragment(private val listener: CategoryFragment.ItemClickedListener) :
    BaseFireFragment(), BadgesAdapter.BadgeClickedListener {
    private lateinit var recyclerView: RecyclerView
    lateinit var collectedBadges: ArrayList<Project>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBadges(userModel.collectedBadges)
    }

    fun getBadges(badges: List<String>?) {
        collectedBadges = ArrayList<Project>()
        initRecyclerView()
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
        val categoryBadges: ArrayList<ArrayList<Project>> = ArrayList()

        for (c in 0..(userModel.categories.size - 1)) {
            categoryBadges.add(ArrayList<Project>())
            for (badge in collectedBadges) {
                if (badge.category == userModel.categories[c]) {
                    categoryBadges[c].add(badge)
                }
            }
        }

        recyclerView = this.requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = BadgeCategoriesAdapter(
            userModel.categories,
            categoryBadges,
            this
        )
        recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onBadgeClicked(badgeId: String) {
        listener.onItemClicked(badgeId, "Univerzális")
        ///ez még szar
        //parentFragmentManager.beginTransaction()
        //    .replace(R.id.fragment_container, DetailsFragment(badgeId, userRefresher = ContainerActivity as UserRefresher), "DetailsFragment").commit()
    }
}