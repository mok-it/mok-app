package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_all_badges_list.*
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class AllBadgesListFragment :
    BaseFireFragment() {

    private val category = "Univerzális" // TODO a megnyitásnál átadni a kategóriát

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_badges_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    /**
     * Tries to load the image provided into the given view. If that did not
     * succeed, it tries to load the default 'broken' image. If that also
     * fails, leaves the image empty and logs an error message.
     */
    private fun loadImage(imageView: ImageView, imageURL: String) {
        if (tryLoadingImage(imageView, imageURL)) return
        if (tryLoadingImage(imageView, getString(R.string.url_no_image))) return
    }

    /**
     * Tries to load an image into the given image view. If for some reason
     * the provided URL does not point to a valid image file, false is returned.
     *
     * @return true if the function succeeded, false if failed
     */
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

    private fun getAdapter(): FirestoreRecyclerAdapter<Project, ProjectViewHolder> {
        val query = firestore.collection(projectCollectionPath).whereEqualTo("category", category)
            .orderBy("created", Query.Direction.DESCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Project>().setQuery(query, Project::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<Project, ProjectViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@AllBadgesListFragment.context)
                    .inflate(R.layout.project_card, parent, false)
                return ProjectViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                model: Project
            ) {
                val tvName: TextView = holder.itemView.findViewById(R.id.projectName)
                val tvDesc: TextView = holder.itemView.findViewById(R.id.projectDescription)
                val ivImg: ImageView = holder.itemView.findViewById(R.id.projectIcon)
                val tvMandatory: TextView = holder.itemView.findViewById(R.id.mandatoryTextView)
                tvName.text = model.name
                tvDesc.text = model.description
                tvMandatory.isVisible = model.mandatory
                loadImage(ivImg, model.icon)

                if (userModel.collectedBadges.contains(model.id)) {
                    holder.itemView.setBackgroundResource(R.drawable.gradient1)
                }

                holder.itemView.setOnClickListener {
                    val action =
                        AllBadgesListFragmentDirections.actionAllBadgesListFragmentToDetailsFragment(
                            model.id
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun initRecyclerView() {
        //TODO ezt vagy firebaseRecyclerAdapterrel vagy NotifyDataChangedel kéne megoldani szépen
        var adapter = getAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = WrapContentLinearLayoutManager(this.context)
        addBadgeButton.setOnClickListener {
            val dialog = CreateBadgeFragment(category)
            dialog.show(parentFragmentManager, "CreateBadgeDialog")
        }
        setAddBadgeButtonVisibility()
        badgeSwipeRefresh.setOnRefreshListener {
            adapter = getAdapter()
            recyclerView.adapter = adapter
            FirebaseUserObject.refreshCurrentUserAndUserModel(
                this.requireContext()
            ) { setAddBadgeButtonVisibility() }
            //TODO nem csak az usert kéne frissíteni, hanem a badgeket is
            // (vszeg a megoldás: firebaseRecyclerAdapter)
            badgeSwipeRefresh.isRefreshing = false
        }
    }

    private fun setAddBadgeButtonVisibility() {
        if (!userModel.isCreator && !userModel.admin) {
            addBadgeButton.visibility = View.INVISIBLE
        } else {
            addBadgeButton.visibility = View.VISIBLE
        }
    }

    interface ItemClickedListener {
        fun onItemClicked(badgeId: String, category: String)
    }
}