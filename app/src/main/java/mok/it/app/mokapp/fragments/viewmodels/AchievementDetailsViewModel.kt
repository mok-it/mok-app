package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User

class AchievementDetailsViewModel(private val achievementId: String) : ViewModel() {
    private var _owners: Flow<List<User>> = AchievementService.getOwners(achievementId)
    public val owners: Flow<List<User>> get() = _owners
    private var _achievement: Flow<Achievement> = AchievementService.getAchievement(achievementId)
    public val achievement: Flow<Achievement> get() = _achievement
    public val owned: Flow<Boolean>
        get() = flowOf(
            userModel.achievements.contains(
                achievementId
            )
        )
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
