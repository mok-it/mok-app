package mok.it.app.mokapp.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentAllMembersBinding

import mok.it.app.mokapp.fragments.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class AllMembersAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<AllMembersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(

            FragmentAllMembersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAllMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /* TODO fix this, binding can't be found
            The app does not use these, this is a
            temporary solution.
         */
        //val idView: TextView = binding.itemNumber
        //val contentView: TextView = binding.content
        lateinit var idView: TextView
        lateinit var contentView: TextView

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}