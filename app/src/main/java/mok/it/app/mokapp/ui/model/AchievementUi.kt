package mok.it.app.mokapp.ui.model

data class AchievementUi(
    val id: String,
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val ownedLevel: Int = 0,
    val maxLevel: Int = 1,
    val mandatory: Boolean = false,
)