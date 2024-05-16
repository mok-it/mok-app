package mok.it.app.mokapp.firebase.model

import com.google.firebase.firestore.DocumentId
import mok.it.app.mokapp.model.Achievement

data class AchievementEntity(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val levelDescriptions: Map<String, String> = mapOf("1" to "Az acsi betöltése nem sikerült. Kérlek próbáld újra később!"),
    val icon: String = "",
    val mandatory: Boolean = false,
) {
    fun toAchievement(): Achievement {
        return Achievement(
            id = id,
            name = name,
            levelDescriptions = levelDescriptions.mapKeys { it.key.toInt() }.toSortedMap(),
            icon = icon,
            mandatory = mandatory,
        )
    }
}
