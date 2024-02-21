package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import mok.it.app.mokapp.databinding.CardMemberBinding
import mok.it.app.mokapp.dialog.ProjectMembersDialogFragment
import mok.it.app.mokapp.model.User


class MembersAdapter(
    private val userArray: Array<User>,
    private val projectMembersDialogFragment: ProjectMembersDialogFragment
) :
    RecyclerView.Adapter<MemberViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MemberViewHolder (
        CardMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val user = userArray[position]
        holder.binding.textView.text = user.name
        val requestOptions = RequestOptions()
        Glide
            .with(context)
            .load(user.photoURL)
            .apply(requestOptions.override(250, 250))
            .apply(RequestOptions.centerCropTransform())
            .apply(RequestOptions.bitmapTransform(RoundedCorners(26)))
            .into(holder.binding.imageView)

        //opening the person's profile if someone clicks on the card
        holder.itemView.setOnClickListener {
            projectMembersDialogFragment.navigateToMemberFragment(user)
        }
    }

    override fun getItemCount() = userArray.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    companion object {
        private const val TAG = "MembersAdapter"
    }
}