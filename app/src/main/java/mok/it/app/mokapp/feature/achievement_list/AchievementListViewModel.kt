package mok.it.app.mokapp.feature.achievement_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.ui.model.AchievementUi

class AchievementListViewModel : ViewModel() {
    private val _achievements: Flow<List<Achievement>> = AchievementService.getAchievements()

    @OptIn(ExperimentalCoroutinesApi::class)
    val achievements: Flow<List<AchievementUi>>
        get() = userModelFlow.flatMapMerge { user ->
            _achievements.map { achievementList ->
                achievementList.map {
                    it.toAchievementUi(user)
                }
            }
        }
}