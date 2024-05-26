package mok.it.app.mokapp.feature.project_add_participants

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

data class AddParticipantsUiState(
    val selectedUserIds: List<String> = emptyList(),
    val selectedUsersChanged: Boolean = false,
)

class AddOrRemoveParticipantsViewModel(val projectId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AddParticipantsUiState())
    val uiState: StateFlow<AddParticipantsUiState> = _uiState.asStateFlow()

    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    val users: LiveData<List<User>> = UserService.getUsers().asLiveData()

    init {
        project.observeForever {
            // initialize the selection
            _uiState.value =
                _uiState.value.copy(selectedUserIds = it.members)
            updateSelectedUsersChanged()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateMembersOfProject() {
        GlobalScope.launch {
            ProjectService.setMembersOfProject(projectId, _uiState.value.selectedUserIds)
        }
    }

    fun userSelectionClicked(user: User) {
        if (_uiState.value.selectedUserIds.contains(user.documentId)) {
            _uiState.value =
                _uiState.value.copy(selectedUserIds = _uiState.value.selectedUserIds - user.documentId)
        } else {
            _uiState.value =
                _uiState.value.copy(selectedUserIds = _uiState.value.selectedUserIds + user.documentId)
        }
        updateSelectedUsersChanged()
    }

    private fun updateSelectedUsersChanged() {
        val selectedUsersChanged =
            _uiState.value.selectedUserIds != project.value?.members
        _uiState.value = _uiState.value.copy(selectedUsersChanged = selectedUsersChanged)
    }
}

@Suppress("UNCHECKED_CAST")
class AddParticipantsViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddOrRemoveParticipantsViewModel::class.java)) {
            return AddOrRemoveParticipantsViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}