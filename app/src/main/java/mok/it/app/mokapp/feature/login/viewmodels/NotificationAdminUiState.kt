package mok.it.app.mokapp.feature.login.viewmodels

import mok.it.app.mokapp.feature.login.NotificationAdminFragment.RadioOption
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

data class NotificationAdminUiState(
        val showDialog: Boolean = false,
        val notificationTitle: String = "",
        val notificationText: String = "",
        val selectedOption: RadioOption = RadioOption.EVERYONE,
        val selectedUsers: List<User> = emptyList(),
        val selectedProjects: List<Project> = emptyList(),
)
