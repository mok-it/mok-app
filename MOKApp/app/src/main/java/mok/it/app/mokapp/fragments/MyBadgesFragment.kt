package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.databinding.FragmentMyBadgesBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.BadgeCategoriesAdapter
import mok.it.app.mokapp.recyclerview.BadgesAdapter


class MyBadgesFragment :
    BaseFireFragment(), BadgesAdapter.BadgeClickedListener {
    private val binding get() = _binding!!
    private var _binding: FragmentMyBadgesBinding? = null

    private lateinit var collectedBadges: ArrayList<Project>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBadges(userModel.collectedBadges)
    }

    private fun getBadges(badges: List<String>?) {
        collectedBadges = ArrayList()
        initRecyclerView()
        badges?.forEach {
            val docRef = Firebase.firestore.collection(Collections.projects).document(it)
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

    private fun initRecyclerView() {
        val categoryBadges: ArrayList<ArrayList<Project>> = ArrayList()

        for (c in 0 until userModel.categoryList.size) {
            categoryBadges.add(ArrayList())
            for (badge in collectedBadges) {
                if (badge.categoryEnum == userModel.categoryList[c]) {
                    categoryBadges[c].add(badge)
                }
            }
        }
        //TODO use FirestoreRecyclerAdapter instead
        binding.recyclerView.adapter = BadgeCategoriesAdapter(
            userModel.categoryList.map { it.toString() },
            categoryBadges,
            this
        )
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onBadgeClicked(badgeId: String) {
        findNavController().navigate(
            MyBadgesFragmentDirections.actionMyBadgesFragmentToDetailsFragment(
                badgeId
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}