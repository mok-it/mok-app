package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
data class User(

    @DocumentId
    val documentId: String = "",

    val admin: Boolean = false,
    val categories: List<String> = ArrayList(),
    val collectedBadges: List<String> = ArrayList(),
    val email: String = "",
    val isCreator: Boolean = false,
    val joinedBadges: List<String> = ArrayList(),
    val name: String = "",
    val photoURL: String = "",
    val uid: String = "",
)