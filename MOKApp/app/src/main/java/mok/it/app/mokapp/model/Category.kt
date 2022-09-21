package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId

class Category(

    @DocumentId
    val id: String = "",

    val name: String = "",
)