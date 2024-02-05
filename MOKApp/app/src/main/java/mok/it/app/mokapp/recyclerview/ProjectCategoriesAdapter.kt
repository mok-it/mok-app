package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project

class ProjectCategoriesAdapter(
    private val categoryNames: List<String>,
    private val collectedBadges: List<List<Project>>,
    private val listener: ProjectsAdapter.ProjectClickedListener
) :

    RecyclerView.Adapter<ProjectCategoriesAdapter.ViewHolder>() {

    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_project_category, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val catNameTv: TextView = viewHolder.itemView.findViewById(R.id.project_category_name)
        val catEmptyTv: TextView = viewHolder.itemView.findViewById(R.id.no_completed_projects)
        val collectedBadgesRV: RecyclerView =
            viewHolder.itemView.findViewById(R.id.completed_projects)

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