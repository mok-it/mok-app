package mok.it.app.mokapp.feature.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.service.MiscService
import mok.it.app.mokapp.ui.model.AchievementUi

class TasksViewModel : ViewModel() {
    private val _achievements = AchievementService.getMandatoryAchievements()

    @OptIn(ExperimentalCoroutinesApi::class)
    val achievements: Flow<List<AchievementUi>>
        get() =
            FirebaseUserObject.userModelFlow.flatMapMerge { user ->
                _achievements.map {
                    it.map { achievement -> achievement.toAchievementUi(user) }
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val projects: Flow<List<Project>>
        get() = FirebaseUserObject.userModelFlow.flatMapMerge { user ->
            ProjectService.getProjectsByIds(user.projectBadges.keys.toList())
        }

    val earnedBadges = FirebaseUserObject.userModelFlow.map {
        it.allBadges
    }

    val requiredBadges = MiscService.getRequiredBadgeCount()
}