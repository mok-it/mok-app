package mok.it.app.mokapp.ui.model

import mok.it.app.mokapp.model.User

data class UserAchievementLevelUi(
    val userId: String = "",
    val name: String = "",
    val photoURL: String = "",
    val ownedLevel: Int = 0
)

fun User.toAchievementLevelUi(ownedLevel: Int = 0): UserAchievementLevelUi {
    return UserAchievementLevelUi(
        userId = documentId,
        name = name,
        photoURL = photoURL,
        ownedLevel = ownedLevel
    )
}