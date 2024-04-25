package mok.it.app.mokapp.feature.project_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

class DetailsViewModel(projectId: String) : ViewModel() {
    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)
    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    val creatorUser: LiveData<User> = project.switchMap { projectParam ->
        if (projectParam.creator.isNotEmpty()) {
            UserService.getUser(projectParam.creator).asLiveData()
        } else {
            MutableLiveData()
        }
    }

    val mostRecentComment = UserService.getMostRecentComment(projectId)
}

class DetailsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}