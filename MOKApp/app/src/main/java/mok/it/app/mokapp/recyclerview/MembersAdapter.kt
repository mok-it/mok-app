package mok.it.app.mokapp.recyclerview

import android.app.Activity
import android.content.Context
import android.telecom.Call
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
import kotlinx.coroutines.withContext
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity
import mok.it.app.mokapp.fragments.ListFragment
import mok.it.app.mokapp.model.User
import kotlin.coroutines.coroutineContext

class MembersAdapter(private val dataSet: List<User>, listener: MemberClickedListener) :
        RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    val listener = listener
    var context: Context? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView
        init {
            textView = view.findViewById(R.id.textView)
            imageView = view.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.member_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var model = dataSet[position]
        viewHolder.textView.text = model.name
        //kép
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(context!!)
            .load(model.photoURL)
            .apply( requestOptions.override(250, 250))
            .into(viewHolder.imageView)

        viewHolder.itemView.setOnLongClickListener {
            listener.onMemberClicked(model.documentId)
            true
        }


    }

    override fun getItemCount() = dataSet.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    interface MemberClickedListener{
        fun onMemberClicked(userId: String)
    }
}