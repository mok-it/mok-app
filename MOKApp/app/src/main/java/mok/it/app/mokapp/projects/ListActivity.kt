package mok.it.app.mokapp.projects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity

class ListActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private lateinit var recyclerView: RecyclerView
    private val TAG = "ListActivity"

    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "/projects";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        /*mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser == null) {
            // TODO handle null user - is this enough?
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            currentUser = mAuth.currentUser!!
        }*/
        mAuth = FirebaseAuth.getInstance()
        button.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val query = firestore.collection(projectCollectionPath)
        val options = FirestoreRecyclerOptions.Builder<ProjectListElement>()
            .setQuery(query, ProjectListElement::class.java)
            .setLifecycleOwner(this).build()
        val adapter =
            object : FirestoreRecyclerAdapter<ProjectListElement, ProjectViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProjectViewHolder {
                    val view = LayoutInflater.from(this@ListActivity)
                        .inflate(R.layout.project_card, parent, false)
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

        recyclerView = findViewById(R.id.recyclerView)

        // Bind data to the list
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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