package mok.it.app.mokapp.projects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.textView2
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity

class ListActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "test";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser == null) {
            // TODO handle null user
        } else {
            currentUser = mAuth.currentUser!!
        }

        //Glide.with(this).load(currentUser?.photoUrl).into(imageView)

        /*button.setOnClickListener{
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val query = firestore.collection(projectCollectionPath);
        val options = FirestoreRecyclerOptions.Builder<ProjectListElement>().setQuery(query, ProjectListElement::class.java)
            .setLifecycleOwner(this).build();
        val adapter = object: FirestoreRecyclerAdapter<ProjectListElement, ProjectViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@ListActivity).inflate(android.R.layout.simple_list_item_2, parent, false);
                return ProjectViewHolder(view);
            }

            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                model: ProjectListElement
            ) {
                // TODO assign the proper ID-s to the variables
                val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvDesc: TextView = holder.itemView.findViewById(android.R.id.text1)
                tvName.text = model.name;
                tvDesc.text = model.description;
                //TODO project icon
                //val imgIcon: Image = ...
            }
        }
        //TODO ez mi?
       /* rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)*/
    }
}