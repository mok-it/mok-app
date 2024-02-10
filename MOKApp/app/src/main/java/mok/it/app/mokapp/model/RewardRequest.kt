package mok.it.app.mokapp.modekkl

import com.google.firebase.firestore.DocumentId
import java.util.*

data class RewardRequest (
    @DocumentId
    val documentId: String = "",

    val user: String = "",
    val reward: String = "",
    val price: String = "",
    val created: Date = Date(),
)