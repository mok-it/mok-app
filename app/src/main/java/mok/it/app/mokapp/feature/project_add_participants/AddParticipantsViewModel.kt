package mok.it.app.mokapp.feature.project_add_participants

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

data class AddParticipantsUiState(
    val selectedUsers: List<String> = emptyList(),
    val selectedUsersChanged: Boolean = false,
)

class AddParticipantsViewModel(val projectId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AddParticipantsUiState())
    val uiState: StateFlow<AddParticipantsUiState> = _uiState.asStateFlow()

    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    val users: LiveData<List<User>> = UserService.getAllUsers()

    init {
        project.observeForever {
            // initialize the selection
            _uiState.value =
                _uiState.value.copy(selectedUsers = it.members)
            updateSelectedUsersChanged()
        }
    }

    fun setMembersOfProject() {
    }

    fun userSelectionClicked(user: User) {
        if (_uiState.value.selectedUsers.contains(user.documentId)) {
            _uiState.value =
                _uiState.value.copy(selectedUsers = _uiState.value.selectedUsers - user.documentId)
        } else {
            _uiState.value =
                _uiState.value.copy(selectedUsers = _uiState.value.selectedUsers + user.documentId)
        }
        updateSelectedUsersChanged()
    }

    private fun updateSelectedUsersChanged() {
        val selectedUsersChanged =
            _uiState.value.selectedUsers != project.value?.members
        _uiState.value = _uiState.value.copy(selectedUsersChanged = selectedUsersChanged)
    }
}

@Suppress("UNCHECKED_CAST")
class AddParticipantsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddParticipantsViewModel::class.java)) {
            return AddParticipantsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}