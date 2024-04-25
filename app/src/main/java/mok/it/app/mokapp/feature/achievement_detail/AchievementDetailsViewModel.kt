package mok.it.app.mokapp.feature.achievement_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.model.AchievementUi

class AchievementDetailsViewModel(private val achievementId: String) : ViewModel() {
    fun grant(achievementId: String) {
        AchievementService.grantAchievement(achievementId, userModel, 1)
    }

    private var _achievement: Flow<Achievement> = AchievementService.getAchievement(achievementId)

    @OptIn(ExperimentalCoroutinesApi::class)
    public val achievement: Flow<AchievementUi>
        get() = userModelFlow.flatMapMerge { user ->
            _achievement.map {
                it.toAchievementUi(user)
            }
        }
    private var _owners: Flow<List<User>> = AchievementService.getOwners(achievementId)
    public val owners: Flow<List<User>> get() = _owners
    public val owned = userModelFlow.map { it.achievements.contains(achievementId) }
}

class AchievementDetailsViewModelFactory(private val achievementId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementDetailsViewModel::class.java)) {
            return AchievementDetailsViewModel(achievementId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
