package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import mok.it.app.mokapp.ui.model.AchievementUi
import java.util.SortedMap

data class Achievement(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val levelDescriptions: SortedMap<Int, String> = sortedMapOf(1 to "Az acsi betöltése nem sikerült. Kérlek próbáld újra később!"),
    val icon: String = "",
    val mandatory: Boolean = false,
) {
    fun toAchievementUi(user: User): AchievementUi {


        val ownedLevel = user.achievements.getOrDefault(id, 0)
        val maxLevel = levelDescriptions.lastKey()
        return AchievementUi(
            id = id,
            name = name,
            ownedLevel = ownedLevel,
            maxLevel = maxLevel,
            description = when {
                (levelDescriptions.isEmpty()) -> "pusztulat"

                (ownedLevel == maxLevel) ->
                    levelDescriptions[maxLevel] ?: "dögrovás"

                (ownedLevel == 0) ->
                    levelDescriptions[1] ?: "rettenet"

                else ->
                    levelDescriptions[ownedLevel + 1] ?: "förmedvény"
            },
            icon = icon,
            mandatory = mandatory,
        )
    }


}
