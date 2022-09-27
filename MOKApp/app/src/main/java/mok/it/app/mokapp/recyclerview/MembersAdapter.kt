package mok.it.app.mokapp.recyclerview

import android.content.Context
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
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.model.User

class MembersAdapter(private val dataSet: ArrayList<User>, private val listener: MemberClickedListener, private val userIsEditor: Boolean) :
        RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    var context: Context? = null

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
        val model = dataSet[position]
        viewHolder.textView.text = model.name
        //kép
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(context!!)
            .load(model.photoURL)
            .apply( requestOptions.override(250, 250))
            .into(viewHolder.imageView)


        if (canAccept()){
            viewHolder.itemView.setOnLongClickListener {
                listener.onMemberClicked(model)
                true
            }
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    //ha creator, editor vagy admin a felhasználó
    private fun canAccept(): Boolean{
        if (userModel.admin || userModel.isCreator || userIsEditor) return true
        return false
    }

    interface MemberClickedListener{
        fun onMemberClicked(userId: User)
    }
}