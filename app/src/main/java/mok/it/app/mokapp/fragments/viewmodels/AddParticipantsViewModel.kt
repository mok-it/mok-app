package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.model.Project

class AddParticipantsViewModel(projectId: String) : ViewModel() {
    val project: LiveData<Project> = MutableLiveData()
//TODO        ProjectService.getProjectData(projectId)
}

class AddParticipantsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddParticipantsViewModel::class.java)) {
            return AddParticipantsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}