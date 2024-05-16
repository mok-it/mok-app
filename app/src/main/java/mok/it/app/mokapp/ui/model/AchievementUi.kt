package mok.it.app.mokapp.ui.model

import java.util.SortedMap

data class AchievementUi(
    val id: String,
    val name: String = "",
    val currentDescription: String? = null,
    val firstDescription: String?,
    val levelDescriptions: SortedMap<Int, String> = sortedMapOf(1 to "Az acsi betöltése nem sikerült. Kérlek próbáld újra később!"),
    val icon: String = "",
    val ownedLevel: Int = 0,
    val maxLevel: Int = 1,
    val mandatory: Boolean = false,
)