package mok.it.app.mokapp.baseclasses

import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import java.text.SimpleDateFormat

open class BaseFireFragment : Fragment() {
    val firestore = Firebase.firestore;
    lateinit var model: Project
    private lateinit var recyclerView: RecyclerView
    var mAuth = FirebaseAuth.getInstance()
    var currentUser = mAuth.currentUser!!

    fun documentOnSuccess(collectionPath : String, document : String,  onSuccesListener : (DocumentSnapshot) -> (Unit)){
        firestore.collection(collectionPath).document(document).get().addOnSuccessListener(onSuccesListener);
    }

    fun initRecyclerView(adapter : RecyclerView.Adapter<MembersAdapter.ViewHolder>){
        recyclerView = this.requireView().findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }
}