package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import mok.it.app.mokapp.databinding.FragmentMyBadgesBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.ProjectCategoriesAdapter
import mok.it.app.mokapp.recyclerview.ProjectsAdapter
import mok.it.app.mokapp.service.ProjectService
import mok.it.app.mokapp.utility.Utility.TAG


class MyBadgesFragment :
    Fragment(), ProjectsAdapter.ProjectClickedListener {
    private val binding get() = _binding!!
    private var _binding: FragmentMyBadgesBinding? = null

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
        ProjectService.getProjectsByIds(
            projectIds = userModel.projectBadges.keys.toList(),
            onComplete = { projectsList ->
                collectedBadges.clear()
                for (project in projectsList) {
                    collectedBadges.add(Pair(project, userModel.projectBadges[project.id] ?: 0))
                }
                initRecyclerView()
            },
            onFailure = { exception ->
                Log.d(TAG, exception.toString())
            }
        )
    }

    private fun initRecyclerView() {
        val categoryProjects: ArrayList<ArrayList<Pair<Project, Int>>> = ArrayList()
        for (c in 0 until Category.toList().size) {
            categoryProjects.add(ArrayList())
            for (projectBadgePair in collectedBadges) {
                if (projectBadgePair.first.categoryEnum.toString() == Category.toList().get(c)) {
                    categoryProjects[c].add(projectBadgePair)
                }
            }
        }
        binding.recyclerView.adapter = ProjectCategoriesAdapter(
            Category.toList().map { it.toString() },
            categoryProjects,
            this
        )
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onProjectClicked(projectId: String) {
        findNavController().navigate(
            MyBadgesFragmentDirections.actionMyBadgesFragmentToDetailsFragment(
                projectId
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}