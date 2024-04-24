package mok.it.app.mokapp.fragments.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.Query
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

class AdminPanelViewModel(projectId: String) : ViewModel() {
    fun addBadges(user: User, badgeValue: Int, onException: (Exception) -> Unit) {
        UserService.addBadges(user.documentId,
            project.value!!.id,
            badgeValue,
            {
                Log.i(
                    TAG,
                    "Badge count of user ${user.documentId} on viewModel.project.value!! ${project.value!!.id} was set to $badgeValue"
                )
                userBadges.value?.set(user.documentId, badgeValue)
            },
            {
                Log.e(
                    TAG, "Could not set badge count " +
                            "on project ${project.value!!.id} for user ${user.documentId}"
                )
                onException.invoke(it)
            })
    }

    fun projectCompleted(userId: String, project: Project) {
        UserService.markProjectAsCompletedForUser(project, userId)

        CloudMessagingService.sendNotificationToUsersById(
            "Projekt teljesítve!",
            "A(z) \"${project.name}\" nevű mancsot sikeresen teljesítetted!",
            listOf(userId)
        )
    }

    fun participantsQuery(): Query {
        return UserService.getParticipantsQuery(
            project.value!!.members
        )
    }

    val project: LiveData<Project> = MutableLiveData()

    //TODO ProjectService.getProjectData(projectId)
    val userBadges: LiveData<MutableMap<String, Int>> =
        UserService.getProjectUsersAndBadges(projectId)
}

class AdminPanelViewModelFactory(private val projectId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPanelViewModel::class.java)) {
            return AdminPanelViewModel(projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}