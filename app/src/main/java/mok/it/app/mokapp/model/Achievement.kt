package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.SortedMap

data class Achievement(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val levelDescriptions: SortedMap<Int, String> = sortedMapOf(),
    val icon: String = "",
    val mandatory: Boolean = false,
)
