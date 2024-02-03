package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.databinding.FragmentMyBadgesBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.BadgeCategoriesAdapter
import mok.it.app.mokapp.recyclerview.BadgesAdapter
import mok.it.app.mokapp.service.IProjectService
import mok.it.app.mokapp.service.IUserService


class MyBadgesFragment :
    Fragment(), BadgesAdapter.BadgeClickedListener {
    private val binding get() = _binding!!
    private var _binding: FragmentMyBadgesBinding? = null
    private val projectService: IProjectService = mok.it.app.mokapp.service.ProjectService

    private var collectedBadges: ArrayList<Pair<Project, Int>> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectService.getProjectsByIds(
            projectIds = userModel.projectBadges.keys.toList(),
            onComplete = { projectsList ->
                collectedBadges.clear()
                for (project in projectsList) {
                    collectedBadges.add(Pair(project, userModel.projectBadges[project.id] ?: 0))
                }
                initRecyclerView()
            },
            onFailure = { exception ->
                Log.d("MANCSAIM", exception.toString())
            }
        )
    }
/*
    private fun getBadges(badges: List<String>?) {
        collectedBadges = ArrayList()
        initRecyclerView()
        badges?.forEach {
            val docRef = Firebase.firestore.collection(Collections.badges).document(it)
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
*/
    private fun initRecyclerView() {
        val categoryBadges: ArrayList<ArrayList<Pair<Project, Int>>> = ArrayList()
        for (c in 0 until Category.toList().size) {
            categoryBadges.add(ArrayList())
            for (projectBadgePair in collectedBadges) {
                if (projectBadgePair.first.categoryEnum.toString() == Category.toList().get(c)) {
                    categoryBadges[c].add(projectBadgePair)
                }
            }
        }
        binding.recyclerView.adapter = BadgeCategoriesAdapter(
            Category.toList().map { it.toString() },
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