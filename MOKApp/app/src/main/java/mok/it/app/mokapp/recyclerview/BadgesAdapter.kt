package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project

class BadgesAdapter(private val dataSet: List<Project>, private val listener: BadgeClickedListener) :
    RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_badge, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val model = dataSet[position]
        val ivImg: ImageView = viewHolder.itemView.findViewById(R.id.imageView)
        loadImage(ivImg, model.icon)

        viewHolder.itemView.setOnClickListener{
            listener.onBadgeClicked(model.id)
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

    interface BadgeClickedListener {
        fun onBadgeClicked(badgeId: String)
    }
}