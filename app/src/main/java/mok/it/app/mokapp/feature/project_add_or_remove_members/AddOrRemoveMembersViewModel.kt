package mok.it.app.mokapp.feature.project_add_or_remove_members

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
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
import mok.it.app.mokapp.utility.Utility.unaccent

data class AddOrRemoveMembersUiState(
        val selectedUserIds: List<String> = emptyList(),
        val selectedUsersChanged: Boolean = false,
)

class AddOrRemoveMembersViewModel(val projectId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AddOrRemoveMembersUiState())
    val uiState: StateFlow<AddOrRemoveMembersUiState> = _uiState.asStateFlow()

    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    val users: LiveData<List<User>> = UserService.getUsers().asLiveData()
    val selectedUsers: LiveData<List<User>> = users.map { users ->
        users.filter { user ->
            _uiState.value.selectedUserIds.contains(user.documentId)
        }
    }

    private val _searchQuery = mutableStateOf("")
    val searchQuery get() = _searchQuery
    private val _chipState = mutableStateOf(ChipTextFieldState<Chip>())
    val chipState get() = _chipState

    init {
        project.observeForever {
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

    val unselectedFilteredUsers
        get() =
            selectedUsers.map { users ->
                users
                        .filter { user ->
                            isUserMatched(
                                    user, searchQuery.value, chipState.value
                            )
                                    && !_uiState.value.selectedUserIds.contains(user.documentId)
                        }
                        .sortedWith(
                                compareBy { it.name }
                        )
            }

    private fun isUserMatched(
            user: User,
            cleanSearchQuery: String,
            chipState: ChipTextFieldState<Chip>,
    ): Boolean {
        val cleanSearchWords =
                chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        // users are searchable by name or nickname
        return cleanSearchWords.all {
            user.name.unaccent().contains(it, ignoreCase = true) || user.nickname.unaccent()
                    .contains(it, ignoreCase = true)
        }
    }

    fun onSearchValueChange(value: String) {
        _searchQuery.value = value
    }
}

@Suppress("UNCHECKED_CAST")
class AddOrRemoveMembersViewModelFactory(private val projectId: String) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddOrRemoveMembersViewModel::class.java)) {
            return AddOrRemoveMembersViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}