package mok.it.app.mokapp.feature.achievement_grant

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.ui.model.UserAchievementLevelUi
import mok.it.app.mokapp.ui.model.toAchievementLevelUi

class GrantAchievementViewModel(achievementId: String) : ViewModel() {
    private val _achevement = AchievementService.getAchievement(achievementId)
    val maxLevel = _achevement.map { it.levelDescriptions.size }

    private var _users: List<UserAchievementLevelUi> =
        listOf(UserAchievementLevelUi("asdf", "Példa Béla", ""))

    init {
        viewModelScope.launch {
            UserService.getUsers().collect { users ->
                _users = users.map { user ->
                    user.toAchievementLevelUi(
                        user.achievements[achievementId] ?: 0
                    )
                }
            }
        }
    }

    val users = mutableStateListOf(*(_users.toTypedArray()))

    fun onEvent(event: EditAchievementEvent) {
        when (event) {
            is EditAchievementEvent.SetAmount -> {
                val u = users.find { it.userId == event.user.userId }
                if (u != null) {
                    users[users.indexOf(u)] = u.copy(ownedLevel = event.amount)
                }
            }

            is EditAchievementEvent.Save -> {
                Log.d("GrantAchievementViewModel", "Saving")
            }
        }
    }
}

sealed class EditAchievementEvent {
    data class SetAmount(val amount: Int, val user: UserAchievementLevelUi) : EditAchievementEvent()
    data object Save : EditAchievementEvent()
}


class GrantAchievementViewModelFactory(private val achievementId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GrantAchievementViewModel::class.java)) {
            return GrantAchievementViewModel(achievementId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}