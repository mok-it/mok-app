package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService

class PhoneBookViewModel : ViewModel() {
    val users: LiveData<List<User>> = UserService.getAllUsers()
}
