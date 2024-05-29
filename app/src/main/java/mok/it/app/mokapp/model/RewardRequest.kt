package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class RewardRequest(
        @DocumentId
        val documentId: String = "",

        val user: String = "",
        val reward: String = "",
        val price: Int = 0,
        val created: Date = Date(),
)