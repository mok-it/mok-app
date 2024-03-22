package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.User

class CommentsViewModel : ViewModel() {
    fun getUserById(userId: String): LiveData<User> = UserService.getUser(userId)
}