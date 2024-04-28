package mok.it.app.mokapp.feature.achievement_create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.model.Achievement
import java.util.SortedMap

class EditAchievementViewModel : ViewModel() {
    val achievement = mutableStateOf(Achievement(levelDescriptions = sortedMapOf(1 to "")))

    fun onEvent(event: EditAchievementEvent) {
        when (event) {
            is EditAchievementEvent.ChangeName -> achievement.value =
                achievement.value.copy(name = event.name)

            is EditAchievementEvent.ChangeLevelDescription -> achievement.value =
                achievement.value.copy(levelDescriptions = event.descriptions.toLevelDescriptions())

            is EditAchievementEvent.ChangeIcon -> achievement.value =
                achievement.value.copy(icon = event.icon)

            is EditAchievementEvent.ChangeMandatory -> achievement.value =
                achievement.value.copy(mandatory = event.mandatory)

            is EditAchievementEvent.Create -> {
                //TODO
            }
        }
    }
}


sealed class EditAchievementEvent {
    data class ChangeName(val name: String) : EditAchievementEvent()
    data class ChangeLevelDescription(val descriptions: List<String>) : EditAchievementEvent()
    data class ChangeIcon(val icon: String) : EditAchievementEvent()
    data class ChangeMandatory(val mandatory: Boolean) : EditAchievementEvent()
    data object Create : EditAchievementEvent()
}

private fun List<String>.toLevelDescriptions(): SortedMap<Int, String> {
    val levelDescriptions = sortedMapOf<Int, String>()
    forEachIndexed { i, d ->
        levelDescriptions[i + 1] = d
    }
    return levelDescriptions
}
