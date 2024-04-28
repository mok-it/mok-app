package mok.it.app.mokapp.feature.achievement_create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import java.util.SortedMap

class EditAchievementViewModel(
    achievement: Achievement = Achievement(levelDescriptions = sortedMapOf(1 to ""))
) : ViewModel() {
    val achievement = mutableStateOf(achievement)

    fun onEvent(event: EditAchievementEvent) {
        when (event) {
            is EditAchievementEvent.ChangeName -> achievement.value =
                achievement.value.copy(name = event.name)

            is EditAchievementEvent.ChangeLevelDescriptions -> achievement.value =
                achievement.value.copy(levelDescriptions = event.descriptions.toLevelDescriptions())

            is EditAchievementEvent.ChangeIcon -> achievement.value =
                achievement.value.copy(icon = event.icon)

            is EditAchievementEvent.ChangeMandatory -> achievement.value =
                achievement.value.copy(mandatory = event.mandatory)

            is EditAchievementEvent.Insert -> {
                AchievementService.insertAchievement(achievement.value.toAchievementEntity())
            }
        }
    }
}


sealed class EditAchievementEvent {
    data class ChangeName(val name: String) : EditAchievementEvent()
    data class ChangeLevelDescriptions(val descriptions: List<String>) : EditAchievementEvent()
    data class ChangeIcon(val icon: String) : EditAchievementEvent()
    data class ChangeMandatory(val mandatory: Boolean) : EditAchievementEvent()
    data object Insert : EditAchievementEvent()
}

class EditAchievementViewModelFactory(private val achievement: Achievement) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditAchievementViewModel::class.java)) {
            return EditAchievementViewModel(achievement) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun List<String>.toLevelDescriptions(): SortedMap<Int, String> {
    val levelDescriptions = sortedMapOf<Int, String>()
    forEachIndexed { i, d ->
        levelDescriptions[i + 1] = d
    }
    return levelDescriptions
}
