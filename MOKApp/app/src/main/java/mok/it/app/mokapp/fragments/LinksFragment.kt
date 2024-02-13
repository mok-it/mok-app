package mok.it.app.mokapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.databinding.CardLinkBinding
import mok.it.app.mokapp.databinding.FragmentLinksBinding
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.recyclerview.LinkViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class LinksFragment : Fragment() {
    private lateinit var _binding: FragmentLinksBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = WrapContentLinearLayoutManager(this.context)
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<mok.it.app.mokapp.model.Link, LinkViewHolder> {
        val query =
            Firebase.firestore.collection(Collections.links)
                .orderBy("title", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<mok.it.app.mokapp.model.Link>()
                .setQuery(query, mok.it.app.mokapp.model.Link::class.java)
                .setLifecycleOwner(this).build()
        return object :
            FirestoreRecyclerAdapter<mok.it.app.mokapp.model.Link, LinkViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LinkViewHolder (
                CardLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            override fun onBindViewHolder(
                holder: LinkViewHolder,
                position: Int,
                model: mok.it.app.mokapp.model.Link
            ) {
                val tvName: TextView = holder.binding.linkName
                tvName.text = model.title
                binding.root.setOnClickListener {
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
