package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService

class DetailsFragmentViewModel(projectId: String) : ViewModel() {
    val members: LiveData<Array<User>> get() = _members
    private val _members: MutableLiveData<Array<User>> by lazy {
        MutableLiveData<Array<User>>()
    }

    val mostRecentComment = UserService.getMostRecentComment(projectId)

    init {
        _members.value = arrayOf()
    }

    fun setMembers(users: Array<User>) {
        _members.value = users
    }
}

class DetailsFragmentViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsFragmentViewModel::class.java)) {
            return DetailsFragmentViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}