package mok.it.app.mokapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.card_member.view.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.dialog.BadgeMembersDialogFragmentDirections
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.User

class MembersAdapter(
    private val userArray: Array<User>,
    private val userIsEditor: Boolean
) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_member, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = userArray[position]
        viewHolder.textView.text = user.name

        val requestOptions = RequestOptions()
        Glide
            .with(context)
            .load(user.photoURL)
            .apply(requestOptions.override(250, 250))
            .apply(RequestOptions.centerCropTransform())
            .apply(RequestOptions.bitmapTransform(RoundedCorners(26)))
            .into(viewHolder.imageView)

        //opening the person's profile if someone clicks on the card
        viewHolder.itemView.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                BadgeMembersDialogFragmentDirections.actionGlobalMemberFragment(
                    user
                )
            )
        )

        if (canAccept()) {
            viewHolder.itemView.btnCompleted.visibility = View.VISIBLE
            viewHolder.itemView.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    BadgeMembersDialogFragmentDirections.actionBadgeAllMemberDialogFragmentToBadgeAcceptMemberDialogFragment(
                        user
                    )
                )
            )
        }
    }

    override fun getItemCount() = userArray.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    //ha creator, editor vagy admin a felhasználó
    private fun canAccept(): Boolean {
        if (userModel.admin || userModel.isCreator || userIsEditor) return true
        return false
    }

    companion object {
        private const val TAG = "MembersAdapter"
    }
}