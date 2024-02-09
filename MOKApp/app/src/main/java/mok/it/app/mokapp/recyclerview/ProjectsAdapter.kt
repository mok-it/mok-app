package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project

class ProjectsAdapter(
    private val dataSet: List<Pair<Project, Int>>,
    private val listener: ProjectClickedListener
) :
    RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_my_project, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val projectValuePair = dataSet[position]
        val ivImg: ImageView = viewHolder.itemView.findViewById(R.id.imageView)
        val tvBadgeName: TextView = viewHolder.itemView.findViewById(R.id.projectName)
        val tvCollectedBadgeCount: TextView = viewHolder.itemView.findViewById((R.id.collectedBadgeCount))
        loadImage(ivImg, projectValuePair.first.icon)
        tvBadgeName.text = projectValuePair.first.name
        tvCollectedBadgeCount.text = projectValuePair.second.toString()

        viewHolder.itemView.setOnClickListener {
            listener.onProjectClicked(projectValuePair.first.id)
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private fun loadImage(imageView: ImageView, imageURL: String): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun interface ProjectClickedListener {
        fun onProjectClicked(projectId: String)
    }
}