package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Link
import mok.it.app.mokapp.utility.Utility.TAG

object MiscService {

    fun getLinks(): LiveData<List<Link>> {
        val linksLiveData: MutableLiveData<List<Link>> = MutableLiveData()
        Firebase.firestore.collection(Collections.LINKS).get()
            .addOnSuccessListener { querySnapshot ->
                val linksList = mutableListOf<Link>()

                for (document in querySnapshot.documents) {
                    val link = document.toObject(Link::class.java)
                    link?.let {
                        linksList.add(it)
                    }
                }

                linksLiveData.value = linksList
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve links: $exception")
            }
        return linksLiveData
    }
}