package mok.it.app.mokapp.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mok.it.app.mokapp.databinding.CardProjectCategoryBinding
import mok.it.app.mokapp.model.Project

class ProjectCategoriesAdapter(
        private val categoryNames: List<String>,
        private val collectedBadges: List<List<Pair<Project, Int>>>,
        private val listener: ProjectsAdapter.ProjectClickedListener
) :

        RecyclerView.Adapter<ProjectCategoryViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
            ProjectCategoryViewHolder(
                    CardProjectCategoryBinding
                            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            )

    override fun onBindViewHolder(holder: ProjectCategoryViewHolder, position: Int) {
        val catNameTv: TextView = holder.binding.projectCategoryName
        val catEmptyTv: TextView = holder.binding.noCompletedProjects
        val collectedBadgesRV: RecyclerView =
                holder.binding.completedProjects

        catNameTv.text = categoryNames[position]

        if (collectedBadges[position].isEmpty()) {
            catEmptyTv.visibility = View.VISIBLE
            collectedBadgesRV.visibility = View.GONE
        } else {
            catEmptyTv.visibility = View.GONE
            collectedBadgesRV.visibility = View.VISIBLE
            collectedBadgesRV.adapter = ProjectsAdapter(collectedBadges[position], listener)
            collectedBadgesRV.layoutManager =
                    GridLayoutManager(this.context, 2, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun getItemCount() = categoryNames.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
}