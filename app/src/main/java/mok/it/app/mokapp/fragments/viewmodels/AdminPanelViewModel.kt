package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project

class AdminPanelViewModel(projectId: String) : ViewModel() {
    val project: LiveData<Project> = ProjectService.getProjectData(projectId)
    val userBadges: LiveData<MutableMap<String, Int>> =
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