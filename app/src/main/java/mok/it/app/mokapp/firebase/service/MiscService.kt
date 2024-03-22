package mok.it.app.mokapp.firebase.service

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import mok.it.app.mokapp.model.Collections

object MiscService : FirebaseMessagingService() {
    fun getLinksQuery() =
        Firebase.firestore.collection(Collections.links)
            .orderBy("title")
}