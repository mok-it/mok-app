package mok.it.app.mokapp.feature.admin_notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import mok.it.app.mokapp.feature.admin_notification.NotificationAdminFragment.RadioOption
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

class NotificationAdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationAdminUiState())
    val uiState: StateFlow<NotificationAdminUiState> = _uiState.asStateFlow()

    val projects: LiveData<List<Project>> = ProjectService.getAllProjects().asLiveData()
    val users: LiveData<List<User>> = UserService.getUsers().asLiveData()

    fun setDialogState(showDialog: Boolean) {
        _uiState.value = _uiState.value.copy(showDialog = showDialog)
    }

    fun sendNotification() {
        TODO("do not send while developing")
        CloudMessagingService.sendNotificationToUsers(
                uiState.value.notificationTitle, uiState.value.notificationText,
                getUsersToSendNotificationTo.value?.toList() ?: emptyList()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val getUsersToSendNotificationTo: LiveData<List<User>> =
            when (uiState.value.selectedOption) {
                RadioOption.EVERYONE -> {
                    users
                }

                RadioOption.EVERYONE_EXCEPT -> {
                    users.asFlow()
                            .flatMapConcat { allUsers ->
                                flow {
                                    emit(allUsers.filter { user ->
                                        !uiState.value.selectedUsers.contains(user)
                                    })
                                }
                            }.asLiveData()
                }

                RadioOption.SPECIFIC_PEOPLE -> {
                    flowOf(uiState.value.selectedUsers).asLiveData()
                }

                RadioOption.PROJECT_MEMBERS -> {
                    uiState.value.selectedProjects.asFlow()
                            .flatMapConcat { project ->
                                if (project.members.isNotEmpty()) {
                                    flow {
                                        emitAll(UserService.getUsers(project.members)) // emit users for each project
                                    }
                                } else {
                                    flowOf(emptyList()) // emit empty list for empty projects
                                }
                            }.asLiveData()
                }
            }

    fun setNotificationTitle(title: String) {
        _uiState.value = _uiState.value.copy(notificationTitle = title)
    }

    fun setNotificationText(text: String) {
        _uiState.value = _uiState.value.copy(notificationText = text)
    }

    fun selectedUserClicked(user: User) {
        val selectedUsers = uiState.value.selectedUsers.toMutableList()
        if (selectedUsers.contains(user)) {
            selectedUsers.remove(user)
        } else {
            selectedUsers.add(user)
        }
        _uiState.value = _uiState.value.copy(selectedUsers = selectedUsers)
    }

    fun selectedProjectClicked(project: Project) {
        val selectedProjects = uiState.value.selectedProjects.toMutableList()
        if (selectedProjects.contains(project)) {
            selectedProjects.remove(project)
        } else {
            selectedProjects.add(project)
        }
        _uiState.value = _uiState.value.copy(selectedProjects = selectedProjects)
    }

    fun onRadioOptionSelected(radioOption: RadioOption) {
        _uiState.value = _uiState.value.copy(selectedOption = radioOption)
    }
}