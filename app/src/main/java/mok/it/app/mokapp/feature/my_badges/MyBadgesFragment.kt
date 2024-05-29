package mok.it.app.mokapp.feature.my_badges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import mok.it.app.mokapp.databinding.FragmentMyBadgesBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.enums.Category
import mok.it.app.mokapp.ui.recyclerview.ProjectCategoriesAdapter
import mok.it.app.mokapp.ui.recyclerview.ProjectsAdapter


class MyBadgesFragment :
        Fragment(), ProjectsAdapter.ProjectClickedListener {
    private val binding get() = _binding!!
    private var _binding: FragmentMyBadgesBinding? = null

    private var collectedBadges: ArrayList<Pair<Project, Int>> = ArrayList()
    private val viewModel: MyBadgesViewModel by viewModels()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.projectsByIds.observe(viewLifecycleOwner) { projectsList ->
            collectedBadges.clear()
            for (project in projectsList) {
                collectedBadges.add(Pair(project, userModel.projectBadges[project.id] ?: 0))
            }
            initRecyclerView()
        }
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
                Category.toList(),
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