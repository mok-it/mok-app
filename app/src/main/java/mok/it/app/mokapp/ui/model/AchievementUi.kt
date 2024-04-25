package mok.it.app.mokapp.ui.model

data class AchievementUi(
    var id: String,
    var name: String = "",
    var description: String = "",
    var icon: String = "",
    var owned: Boolean = false,
    val mandatory: Boolean = false,
)