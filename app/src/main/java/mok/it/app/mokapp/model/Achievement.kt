package mok.it.app.mokapp.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import mok.it.app.mokapp.firebase.model.AchievementEntity
import mok.it.app.mokapp.ui.model.AchievementUi
import java.util.SortedMap

@Parcelize
data class Achievement(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val levelDescriptions: SortedMap<Int, String> = sortedMapOf(1 to "Az acsi betöltése nem sikerült. Kérlek próbáld újra később!"),
    val icon: String = "",
    val mandatory: Boolean = false,
) : Parcelable {
    fun toAchievementUi(user: User): AchievementUi {


        val ownedLevel = user.achievements.getOrDefault(id, 0)
        val maxLevel = levelDescriptions.lastKey()
        return AchievementUi(
            id = id,
            name = name,
            ownedLevel = ownedLevel,
            maxLevel = maxLevel,
            firstDescription = levelDescriptions[1],
            currentDescription = when {
                (levelDescriptions.isEmpty()) -> null
                (ownedLevel == maxLevel) -> levelDescriptions[maxLevel]
                (ownedLevel == 0) -> levelDescriptions[1]
                else -> levelDescriptions[ownedLevel + 1]
            },
            levelDescriptions = levelDescriptions,
            icon = icon,
            mandatory = mandatory,
        )
    }

    fun toAchievementEntity(): AchievementEntity {
        val r = AchievementEntity(
            id = id,
            name = name,
            levelDescriptions = levelDescriptions.mapKeys { it.key.toString() },
            icon = icon,
            mandatory = mandatory,
        )
        return r
    }
}
