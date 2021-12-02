package mok.it.app.mokapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

class ListFragment : Fragment() {

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
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }
}