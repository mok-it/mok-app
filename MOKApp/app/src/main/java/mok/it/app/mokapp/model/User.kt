package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
data class User(

    @DocumentId
    val documentId: String = "",

    val email: String = "",
    val id: String = "",
    val isCreator: Boolean = false,
    val isOwner: Boolean = false,
    val name: String = "",
    val photoURL: String = "",
)