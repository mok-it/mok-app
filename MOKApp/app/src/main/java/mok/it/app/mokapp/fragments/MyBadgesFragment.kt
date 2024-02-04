package mok.it.app.mokapp.fragments

import android.os.Bundle
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
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.ProjectCategoriesAdapter
import mok.it.app.mokapp.recyclerview.ProjectsAdapter


class MyBadgesFragment :
    Fragment(), ProjectsAdapter.ProjectClickedListener {
    private val binding get() = _binding!!
    private var _binding: FragmentMyBadgesBinding? = null

    private lateinit var completedProjects: ArrayList<Project>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProjects(userModel.collectedBadges)
    }

    private fun getProjects(projectIds: List<String>?) {
        completedProjects = ArrayList()
        initRecyclerView()
        projectIds?.forEach {
            val docRef = Firebase.firestore.collection(Collections.projects).document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val badge = document.toObject(Project::class.java)!!
                        completedProjects.add(badge)
                        if (projectIds.size == completedProjects.size) {
                            initRecyclerView()
                        }
                    }
                }
        }
    }

    private fun initRecyclerView() {
        val categoryProjects: ArrayList<ArrayList<Project>> = ArrayList()

        for (c in 0 until userModel.categoryList.size) {
            categoryProjects.add(ArrayList())
            for (project in completedProjects) {
                if (project.categoryEnum == userModel.categoryList[c]) {
                    categoryProjects[c].add(project)
                }
            }
        }
        binding.recyclerView.adapter = ProjectCategoriesAdapter(
            userModel.categoryList.map { it.toString() },
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