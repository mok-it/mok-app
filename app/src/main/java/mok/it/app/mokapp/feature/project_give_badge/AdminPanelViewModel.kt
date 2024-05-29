package mok.it.app.mokapp.feature.project_give_badge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

data class AdminPanelUiState(
        val stateModified: Boolean = false,
        /**
         * userId -> badgeValue (on the current project) mapping
         */
        val sliderValues: Map<String, Int> = emptyMap(),
)

class AdminPanelViewModel(val projectId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()

    val members: LiveData<List<User>> = UserService.getMembersForProject(projectId)
    val project: LiveData<Project> = ProjectService.getProjectData(projectId).asLiveData()
    private val userBadges: LiveData<Map<String, Int>> =
            UserService.getProjectUsersAndBadges(projectId).asLiveData()

    init {
        // Ha egy user megszerzett mancsainak értéke megváltozik, miközben valamelyik slider módosítva lett,
        // akkor a "reset" gomb furcsán viselkedhet (enabled, de ha rányomsz, a jelenlegi állapotra resetel).
        // Ez viszont annyira edge case és apróság, hogy jelenleg nem foglalkozunk vele.
        userBadges.observeForever {
            _uiState.value = _uiState.value.copy(sliderValues = it)
            updateStateModified()
        }
    }

    private fun updateStateModified() {
        val isModified = _uiState.value.sliderValues.any { (userId, value) ->
            userBadges.value?.get(userId) != value
        } // if any of the of the sliders' value is different from the user's badge count
        _uiState.value = _uiState.value.copy(stateModified = isModified)
    }

    fun updateSliderValue(userId: String, value: Int) {
        _uiState.value = _uiState.value.copy(
                sliderValues = _uiState.value.sliderValues + (userId to value)
        )
        updateStateModified()
    }

    fun resetSliderValues() {
        _uiState.value = _uiState.value.copy(
                sliderValues = userBadges.value ?: emptyMap()
        )
        updateStateModified()
    }

    fun saveAllUserBadges() {
        UserService.setProjectBadgesOfMultipleUsers(
                projectId = projectId,
                userIdToBadgeValueMap = _uiState.value.sliderValues
        )

        members.value?.let { membersList ->
            CloudMessagingService.sendNotificationToUsers(
                    title = "Mancso(ka)t kaptál",
                    messageBody = "Gratulálunk! A(z) ${project.value?.name ?: "Ismeretlen nevű"}" +
                            " projektben ${membersList.size} db mancsot szereztél!",
                    adresseeUserList = membersList
            )
        } ?: Log.e(TAG, "Members is null when trying to send notification")
    }

}

class AdminPanelViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPanelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminPanelViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}