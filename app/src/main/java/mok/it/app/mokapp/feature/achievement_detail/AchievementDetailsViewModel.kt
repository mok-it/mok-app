package mok.it.app.mokapp.feature.achievement_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.ui.model.AchievementUi
import java.util.SortedMap

class AchievementDetailsViewModel(private val achievementId: String) : ViewModel() {
    val isUserAdmin: Boolean
        get() = userModel.roleAtLeast(Role.ADMIN)
    private val _achievement: Flow<Achievement> = AchievementService.getAchievement(achievementId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val achievement: Flow<AchievementUi> = userModelFlow.flatMapMerge { user ->
        _achievement.map {
            it.toAchievementUi(user)
        }
    }
    val achievementModel: Flow<Achievement> get() = _achievement

    private val _owners: Flow<List<User>> = AchievementService.getOwners(achievementId)
    val owners: Flow<SortedMap<Int, List<User>>> = _owners.transform { users ->
        val o = sortedMapOf<Int, List<User>>()
        users.onEach { user ->
            val level = user.achievements[achievementId] ?: 0
            o[level] = o[level]?.plus(user) ?: listOf(user)
        }
        emit(o)
    }
}

class AchievementDetailsViewModelFactory(private val achievementId: String) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AchievementDetailsViewModel(achievementId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
