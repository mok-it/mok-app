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
import mok.it.app.mokapp.databinding.CardMyProjectBinding
import mok.it.app.mokapp.model.Project

class ProjectsAdapter(
    private val dataSet: List<Pair<Project, Int>>,
    private val listener: ProjectClickedListener
) :
    RecyclerView.Adapter<MyProjectViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = MyProjectViewHolder(
         CardMyProjectBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    )

    override fun onBindViewHolder(viewHolder: MyProjectViewHolder, position: Int) {
        val projectValuePair = dataSet[position]
        val ivImg: ImageView = viewHolder.binding.imageView
        val tvBadgeName: TextView = viewHolder.binding.projectName
        val tvCollectedBadgeCount: TextView = viewHolder.binding.collectedBadgeCount
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