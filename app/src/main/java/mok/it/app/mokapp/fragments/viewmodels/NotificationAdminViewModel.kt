package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.ProjectService
import mok.it.app.mokapp.service.UserService

class NotificationAdminViewModel : ViewModel() {
    val projects: LiveData<List<Project>> = ProjectService.getAllProjects()
    val users: LiveData<List<User>> = UserService.getAllUsers()
    fun sendNotification(title: String, text: String, users: Set<User>) {
        MyFirebaseMessagingService.sendNotificationToUsers(title, text, users.toList())
    }

    private fun getAllMembersOfProjects(selectedProjects: Set<Project>): MutableSet<User> {
        val users = mutableSetOf<User>()
        selectedProjects.forEach { project ->
            project.members.forEach { userId ->
                UserService.getUserById(userId).observeForever { user ->
                    users.add(user)
                }
            }
        }
        return users
    }

    fun getUsersToSendNotificationTo(
        selectedOption: String,
        radioOptions: List<String>,
        users: LiveData<List<User>>,
        selectedUsers: MutableSet<User>,
        selectedProjects: Set<Project>
    ): MutableSet<User> {
        return when (selectedOption) {
            radioOptions[0] -> {
                users.value?.toMutableSet() ?: mutableSetOf()
            }

            radioOptions[1] -> {
                users.value?.toMutableSet()
                    ?.apply { removeAll(selectedUsers) } ?: mutableSetOf()
            }

            radioOptions[2] -> {
                selectedUsers
            }

            else -> {
                getAllMembersOfProjects(selectedProjects)
            }
        }
    }
}