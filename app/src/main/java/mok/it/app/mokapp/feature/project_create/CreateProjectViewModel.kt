package mok.it.app.mokapp.feature.project_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.User

class CreateProjectViewModel : ViewModel() {
    val allUsers: LiveData<List<User>> = UserService.getAllUsers()
}