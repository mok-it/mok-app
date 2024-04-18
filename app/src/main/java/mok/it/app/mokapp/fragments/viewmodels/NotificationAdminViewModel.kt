package mok.it.app.mokapp.fragments.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.fragments.NotificationAdminFragment.RadioOption
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.ProjectService
import mok.it.app.mokapp.service.UserService

class NotificationAdminViewModel : ViewModel() {

    val projects: LiveData<List<Project>> = ProjectService.getAllProjects()
    val users: LiveData<List<User>> = UserService.getAllUsers()
    val selectedProjects = mutableStateListOf<Project>()

    fun sendNotification(title: String, text: String, users: Set<User>) {
        MyFirebaseMessagingService.sendNotificationToUsers(title, text, users.toList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUsersToSendNotificationTo(
        selectedOption: RadioOption,
        selectedUsers: List<User>,
    ): MutableSet<User> {
        return when (selectedOption) {
            RadioOption.EVERYONE -> {
                users.value?.toMutableSet() ?: mutableSetOf()
            }

            RadioOption.EVERYONE_EXCEPT -> {
                users.value?.toMutableSet()
                    ?.apply { removeAll(selectedUsers.toSet()) } ?: mutableSetOf()
            }

            RadioOption.SPECIFIC_PEOPLE -> {
                selectedUsers.toMutableSet()
            }

            RadioOption.PROJECT_MEMBERS -> {
                selectedProjects.asFlow()
                    .flatMapConcat { project ->
                        if (project.members.isNotEmpty()) {
                            flow {
                                emitAll(UserService.getUsersById(project.members)) // emit users for each project
                            }
                        } else {
                            flowOf(emptyList()) // emit empty list for empty projects
                        }
                    }.asLiveData().value?.toMutableSet() ?: mutableSetOf()
            }
        }
    }
}