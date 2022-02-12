package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
data class Comment(

    @DocumentId
    val collectionId: String = "",

    val text: String = "",
    val time: Timestamp = Timestamp.now(),
    val uid: String = "",
)