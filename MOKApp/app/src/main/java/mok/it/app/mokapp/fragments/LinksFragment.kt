package mok.it.app.mokapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.rpc.Help.Link
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.fragment_all_badges_list.*
import kotlinx.android.synthetic.main.fragment_all_badges_list.view.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.getIconFileName
import mok.it.app.mokapp.recyclerview.LinkViewHolder
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LinksFragment : BaseFireFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_links, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        var adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = adapter
        recyclerView.layoutManager = WrapContentLinearLayoutManager(this.context)
    }
    private fun getAdapter(): FirestoreRecyclerAdapter<mok.it.app.mokapp.model.Link, LinkViewHolder> {
        val query = firestore.collection(linkCollectionPath).orderBy("title", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<mok.it.app.mokapp.model.Link>().setQuery(query, mok.it.app.mokapp.model.Link::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<mok.it.app.mokapp.model.Link, LinkViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
                val view = LayoutInflater.from(this@LinksFragment.context)
                    .inflate(R.layout.card_link, parent, false)
                return LinkViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: LinkViewHolder,
                position: Int,
                model: mok.it.app.mokapp.model.Link
            ) {
                val tvName: TextView = holder.itemView.findViewById(R.id.linkName)
                tvName.text = model.title
                holder.itemView.setOnClickListener {
                    openLink(model.url)
                }
            }
        }
    }
    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
