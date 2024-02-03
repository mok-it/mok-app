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

class BadgeCategoriesAdapter(
    private val categoryNames: List<String>,
    private val collectedBadges: List<List<Pair<Project, Int>>>,
    private val listener: BadgesAdapter.BadgeClickedListener
) :

    RecyclerView.Adapter<BadgeCategoriesAdapter.ViewHolder>() {

    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_badge_category, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val catNameTv: TextView = viewHolder.itemView.findViewById(R.id.badge_category_name)
        val catEmptyTv: TextView = viewHolder.itemView.findViewById(R.id.no_collected_badge)
        val collectedBadgesRV: RecyclerView =
            viewHolder.itemView.findViewById(R.id.collected_badges)

        catNameTv.text = categoryNames[position]

        if (collectedBadges[position].isEmpty()) {
            catEmptyTv.visibility = View.VISIBLE
            collectedBadgesRV.visibility = View.GONE
        } else {
            catEmptyTv.visibility = View.GONE
            collectedBadgesRV.visibility = View.VISIBLE
            collectedBadgesRV.adapter = BadgesAdapter(collectedBadges[position], listener)
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