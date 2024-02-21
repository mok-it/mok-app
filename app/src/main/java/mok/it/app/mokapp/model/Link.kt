package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId

data class Link(
    @DocumentId
    val id: String = "",

    val title: String = "",
    val url: String = "",
    val category: String = "",
)