package mok.it.app.mokapp.feature.phonebook

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.User

class PhoneBookViewModel : ViewModel() {
    val users: LiveData<List<User>> = UserService.getUsers().asLiveData()
}
