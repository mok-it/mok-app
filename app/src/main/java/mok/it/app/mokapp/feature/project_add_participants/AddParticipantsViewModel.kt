package mok.it.app.mokapp.feature.project_add_participants

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project

class AddParticipantsViewModel(projectId: String) : ViewModel() {
    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
}

class AddParticipantsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddParticipantsViewModel::class.java)) {
            return AddParticipantsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}