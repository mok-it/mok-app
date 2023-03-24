package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId

data class Reward(
    @DocumentId
    val documentId: String = "",

    val name: String = "",
    val price: Int = 0,
    val icon: String = "",
)