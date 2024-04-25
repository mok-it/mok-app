package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User

class AchievementDetailsViewModel(private val achievementId: String) : ViewModel() {
    fun grant(achievement: Achievement) {
        AchievementService.grantAchievement(achievement, userModel, 1)
    }

    private var _achievement: Flow<Achievement> = AchievementService.getAchievement(achievementId)
    public val achievement: Flow<Achievement> get() = _achievement
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
