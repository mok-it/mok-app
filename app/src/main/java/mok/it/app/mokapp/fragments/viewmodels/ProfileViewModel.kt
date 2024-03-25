package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.service.UserService

class ProfileViewModel : ViewModel() {

    val userBadgeDataInEachCategory = UserService.getBadgeSumForUserInEachCategory(
        userId = userModel.documentId,
    )
}

