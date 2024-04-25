package mok.it.app.mokapp.feature.achievement_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement

class AchievementListViewModel : ViewModel() {
    private val _achievements: Flow<List<Achievement>> = AchievementService.getAchievements()
    val achievements: Flow<List<Achievement>> get() = _achievements

    fun isOwned(achievement: Achievement): Flow<Boolean> {
        return userModelFlow.map {
            it.achievements.contains(achievement.id)
        }
    }
}