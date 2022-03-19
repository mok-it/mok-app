package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.currentUser
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class CategoryFragment(val listener: ItemClickedListener, val category: String) : BaseFireFragment() {

    private lateinit var recyclerView: RecyclerView
    //lateinit var userModel: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        //getUser(currentUser!!.uid)
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

    fun initRecyclerView(){
        val query = firestore.collection(projectCollectionPath).whereEqualTo("category", category)
        val options = FirestoreRecyclerOptions.Builder<Project>().setQuery(query, Project::class.java)
            .setLifecycleOwner(this).build()
        val adapter = object: FirestoreRecyclerAdapter<Project, ProjectViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@CategoryFragment.context).inflate(R.layout.project_card, parent, false)
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
                tvName.text = model.name
                tvDesc.text = model.description
                loadImage(ivImg, model.icon)

                if (userModel.collectedBadges.contains(model.id)){
                    holder.itemView.setBackgroundResource(R.drawable.gradient1)
                }

                holder.itemView.setOnClickListener{
                    listener.onItemClicked(model.id, category)
                }
            }
        }

        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    fun getUser(uid: String) {
        Firebase.firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userModel = document.toObject(User::class.java)!!
                    initRecyclerView()
                }
            }
    }

    interface ItemClickedListener{
        fun onItemClicked(badgeId: String, category: String)
    }
}