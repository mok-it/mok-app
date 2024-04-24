package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import mok.it.app.mokapp.firebase.service.CommentService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.User

class CommentsViewModel : ViewModel() {
    fun getUserById(userId: String): LiveData<User> = UserService.getUser(userId).asLiveData()
    fun getComments(projectId: String): LiveData<List<Comment>> =
        CommentService.getComments(projectId)
}