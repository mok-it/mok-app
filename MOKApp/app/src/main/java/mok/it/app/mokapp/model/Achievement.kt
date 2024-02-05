package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Achievement(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val mandatory: Boolean = false,
)
