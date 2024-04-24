package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement

class AchievementListViewModel : ViewModel() {
    private val _achievements: Flow<List<Achievement>> = AchievementService.getAchievements()
    val achievements: Flow<List<Achievement>> get() = _achievements

    fun isOwned(achievement: Achievement): Flow<Boolean> {
        return flowOf(userModel.achievements.contains(achievement.id))
    }
}