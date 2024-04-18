package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.User

class DetailsViewModel(projectId: String) : ViewModel() {

    val mostRecentComment = UserService.getMostRecentComment(projectId)

    val project = ProjectService.getProjectData(projectId)

    //TODO only get the creator user when the project is not null
    val creatorUser: LiveData<User>
        get() = project.value?.let { UserService.getUser(it.creator) } ?: MutableLiveData()
    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)

}

class DetailsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}