package mok.it.app.mokapp.feature.project_give_badge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

data class AdminPanelUiState(val stateModified: Boolean = false)
class AdminPanelViewModel(projectId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()
    fun addBadges(user: User, badgeValue: Int, onException: (Exception) -> Unit) {
        UserService.addBadges(user.documentId,
            project.value!!.id,
            badgeValue,
            {
                Log.i(
                    TAG,
                    "Badge count of user ${user.documentId} on viewModel.project.value!! ${project.value!!.id} was set to $badgeValue"
                )
                userBadges.value?.set(user.documentId, badgeValue)
            },
            {
                Log.e(
                    TAG, "Could not set badge count " +
                            "on project ${project.value!!.id} for user ${user.documentId}"
                )
                onException.invoke(it)
            })
    }

    fun projectCompleted(userId: String, project: Project) {
        UserService.markProjectAsCompletedForUser(project, userId)

        CloudMessagingService.sendNotificationToUsersById(
            "Projekt teljesítve!",
            "A(z) \"${project.name}\" nevű mancsot sikeresen teljesítetted!",
            listOf(userId)
        )
    }

    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)
    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    private val userBadges: LiveData<MutableMap<String, Int>> =
        UserService.getProjectUsersAndBadges(projectId)
}

class AdminPanelViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPanelViewModel::class.java)) {
            return AdminPanelViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}