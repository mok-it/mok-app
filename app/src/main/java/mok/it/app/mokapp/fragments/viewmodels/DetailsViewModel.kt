package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.User

class DetailsViewModel(projectId: String) : ViewModel() {
    val members: LiveData<Array<User>> get() = _members
    private val _members: MutableLiveData<Array<User>> by lazy {
        MutableLiveData<Array<User>>()
    }

    val mostRecentComment = UserService.getMostRecentComment(projectId)
    val project = ProjectService.getProjectData(projectId)
    val creatorUser: LiveData<User> get() = UserService.getUser(project.value!!.creator)

    init {
        _members.value = arrayOf()
    }

    fun setMembers(users: Array<User>) {
        _members.value = users
    }
}

class DetailsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}