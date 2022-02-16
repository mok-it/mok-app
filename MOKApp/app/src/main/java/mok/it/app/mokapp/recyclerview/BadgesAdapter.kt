package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

class BadgesAdapter(private val dataSet: List<Project>, listener: BadgeClickedListener) :
    RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    val listener = listener
    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.badge_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var model = dataSet[position]
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

    private fun loadImage(imageView: ImageView, imageURL: String) {
        if (tryLoadingImage(imageView, imageURL)) return
    }

    private fun tryLoadingImage(imageView: ImageView, imageURL: String): Boolean {
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