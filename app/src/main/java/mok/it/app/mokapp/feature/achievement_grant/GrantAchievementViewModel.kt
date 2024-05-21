package mok.it.app.mokapp.feature.achievement_grant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.ui.model.UserAchievementLevelUi
import mok.it.app.mokapp.ui.model.toAchievementLevelUi

class GrantAchievementViewModel(private val achievementId: String) : ViewModel() {
    private val _achevement = AchievementService.getAchievement(achievementId)

    private val _maxLevel = MutableStateFlow(2)
    val maxLevel = _maxLevel.asStateFlow()

    private var _users: MutableStateFlow<List<UserAchievementLevelUi>> =
        MutableStateFlow(emptyList())

    init {
        _achevement.map { it.levelDescriptions.size }.asLiveData().observeForever {
            _maxLevel.value = it
        }
        UserService.getUsers().asLiveData().observeForever {
            _users.value = it.map { user ->
                user.toAchievementLevelUi(ownedLevel = user.achievements[achievementId] ?: 0)
            }
        }
    }

    val users = _users.asStateFlow()

    fun onEvent(event: GrantAchievementEvent) {
        when (event) {
            is GrantAchievementEvent.SetAmount -> {
                val u = _users.value.find { it.userId == event.user.userId }
                if (u != null) {
                    _users.value =
                        _users.value.map { x ->
                            if (x.userId == u.userId) x.copy(
                                ownedLevel =
                                if (event.amount > maxLevel.value) maxLevel.value
                                else if (event.amount < 0) 0
                                else event.amount
                            )
                            else x
                        }
                }
            }

            is GrantAchievementEvent.Save -> {
                val levels = _users.value.map { it.userId to it.ownedLevel }.toMap()
                AchievementService.grantAchievement(achievementId, levels)
            }
        }
    }
}

sealed class GrantAchievementEvent {
    data class SetAmount(val amount: Int, val user: UserAchievementLevelUi) :
        GrantAchievementEvent()

    data object Save : GrantAchievementEvent()
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