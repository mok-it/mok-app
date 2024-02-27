package mok.it.app.mokapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

//the fields of the class should exactly match the fields in Firestore DB
data class Comment(

    @DocumentId
    val collectionId: String = "",

    val text: String = "",
    val time: Timestamp = Timestamp.now(),
    val uid: String = "",
    val userName: String = "",
)