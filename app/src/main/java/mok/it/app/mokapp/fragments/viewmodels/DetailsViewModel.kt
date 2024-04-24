package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User


//
//    val creatorUser: LiveData<User>
//        get() = project.value?.let { UserService.getUser(it.creator) } ?: MutableLiveData()
//    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)
//    //TODO this does not work; it should fetch when the project has value
class DetailsViewModel(projectId: String) : ViewModel() {


    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)
    val project: LiveData<Project?> = ProjectService.getProjectData(projectId)
    val creatorUser: LiveData<User> = project.switchMap { _ ->
        if (project.value != null) {
            UserService.getUser(project.value!!.creator)
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