package mok.it.app.mokapp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.fragment_category.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Filter
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.getIconFileName
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CategoryFragment(val listener: ItemClickedListener, val category: String, val filter: Filter) : BaseFireFragment() {
    private val TAG = "CategoryFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
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
    private fun loadImage(imageView: ImageView, imageURL: String, callback: Callback) {
        if (tryLoadingImage(imageView, imageURL, callback)) return
        if (tryLoadingImage(imageView, getString(R.string.url_no_image), callback)) return
    }

    /**
     * Tries to load an image into the given image view. If for some reason
     * the provided URL does not point to a valid image file, false is returned.
     *
     * @return true if the function succeeded, false if failed
     */
    private fun tryLoadingImage(imageView: ImageView, imageURL: String, callback: Callback): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView, callback)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<Project, ProjectViewHolder> {
        val query = getFilteredQuery()
        val options =
            FirestoreRecyclerOptions.Builder<Project>().setQuery(query, Project::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<Project, ProjectViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@CategoryFragment.context)
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

                val iconFileName = getIconFileName(model.icon)
                val iconFile = File(context?.filesDir, iconFileName)
                if (iconFile.exists()){
                    Log.i(TAG, "loading badge icon " + iconFile.path)
                    val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
                    ivImg.setImageBitmap(bitmap)
                }
                else{
                    Log.i(TAG, "downloading badge icon " + model.icon)
                    val callback = object: Callback {
                        override fun onSuccess() {
                            // save image
                            Log.i(TAG, "saving badge icon " + iconFile.path)
                            val bitmap : Bitmap = ivImg.drawable.toBitmap()
                            var fos: FileOutputStream?
                            try {
                                fos = FileOutputStream(iconFile)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                fos.flush()
                                fos.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    loadImage(ivImg, model.icon, callback)
                }

                if (userModel.collectedBadges.contains(model.id)){
                    holder.itemView.setBackgroundResource(R.drawable.gradient1)
                }

                holder.itemView.setOnClickListener {
                    listener.onItemClicked(model.id, category)
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
            ContainerActivity.refreshCurrentUser(this.requireContext(),
                { setAddBadgeButtonVisibility() })
            //TODO nem csak az usert kéne frissíteni, hanem a badgeket is
            // (vszeg a megoldás: firebaseRecyclerAdapter)
            badgeSwipeRefresh.isRefreshing = false
        }
    }

    private fun getFilteredQuery(): Query{
        var query = firestore.collection(projectCollectionPath).whereEqualTo("category", category)
            .orderBy("created", Query.Direction.DESCENDING)
        if (filter.mandatory){
            query = query.whereEqualTo("mandatory", true)
        }
        if (filter.joined){
            query = query.whereArrayContains("members", userModel.uid)
        }
        if (filter.achieved){
            query = query.whereIn(FieldPath.documentId(), userModel.collectedBadges)
        }
        if (filter.edited){
            query = query.whereArrayContains("editors", userModel.uid)
        }
        return query
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