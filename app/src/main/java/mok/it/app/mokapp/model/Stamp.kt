package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId

data class Stamp(
    @DocumentId
    val documentId: String = "",

    val name: String = "",
    val icon: String = "",
)