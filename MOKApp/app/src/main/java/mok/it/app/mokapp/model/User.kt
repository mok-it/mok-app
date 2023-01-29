package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

//the fields of the class should exactly match the fields in Firestore DB
data class User(
    @DocumentId
    val documentId: String = "",

    val admin: Boolean = false,
    val categories: List<String> = ArrayList(),
    val collectedBadges: List<String> = ArrayList(),
    val email: String = "",
    val joinedBadges: List<String> = ArrayList(),
    val name: String = "",
    @get:PropertyName("isCreator")
    val isCreator: Boolean = false,
    val photoURL: String = "",
    val phoneNumber: String = "",

    //redundant to use (user documentId instead),
    // but since some (at the time of this writing, all) documents have it, it is required here:
    val uid: String = "",
)