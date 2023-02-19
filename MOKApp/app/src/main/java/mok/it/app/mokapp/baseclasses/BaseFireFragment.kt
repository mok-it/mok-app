package mok.it.app.mokapp.baseclasses

import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Project

open class BaseFireFragment : Fragment() {
    //TODO ez a class többet árt, mint segít sztem, kitörölni
    val projectCollectionPath: String = "/projects"
    val userCollectionPath: String = "/users"
    val linkCollectionPath: String = "/links"
    val firestore = Firebase.firestore

    lateinit var model: Project

    fun documentOnSuccess(
        collectionPath: String,
        document: String,
        onSuccesListener: (DocumentSnapshot) -> (Unit)
    ) {
        firestore.collection(collectionPath).document(document).get()
            .addOnSuccessListener(onSuccesListener)
    }
}