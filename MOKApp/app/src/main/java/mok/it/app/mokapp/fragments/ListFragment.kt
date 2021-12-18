package mok.it.app.mokapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity
import mok.it.app.mokapp.projects.ProjectListElement
import mok.it.app.mokapp.projects.ProjectViewHolder
import android.R.string.no
import android.util.Log
import com.squareup.picasso.Picasso


class ListFragment : Fragment() {

    private val TAG = "ListActivity"
    private lateinit var recyclerView: RecyclerView
    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "/projects";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = firestore.collection(projectCollectionPath)
        val options = FirestoreRecyclerOptions.Builder<ProjectListElement>().setQuery(query, ProjectListElement::class.java)
            .setLifecycleOwner(this).build()
        val adapter = object: FirestoreRecyclerAdapter<ProjectListElement, ProjectViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@ListFragment.context).inflate(R.layout.project_card, parent, false)
                return ProjectViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                model: ProjectListElement
            ) {
                val tvName: TextView = holder.itemView.findViewById(R.id.projectName)
                val tvDesc: TextView = holder.itemView.findViewById(R.id.projectDescription)
                val ivImg: ImageView = holder.itemView.findViewById(R.id.projectIcon)
                tvName.text = model.name
                tvDesc.text = model.description

                loadImage(ivImg, model.iconPath)
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
        //recyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    /**
     * Tries to load the image provided into the given view. If that did not
     * succeed, it tries to load the default 'broken' image. If that also
     * fails, leaves the image empty and logs an error message.
     */
    private fun loadImage(imageView: ImageView, imageURL: String) {

        if (tryLoadingImage(imageView, imageURL)) return
        if (tryLoadingImage(imageView, getString(R.string.url_no_image))) return

        Log.e(TAG, "Both the provided and the default image failed to load. Leaving empty.")

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
            Log.w(TAG, "Image not found: $imageURL")
            Log.w(TAG, "Picasso message: " + e.message)
            false
        }
    }
}