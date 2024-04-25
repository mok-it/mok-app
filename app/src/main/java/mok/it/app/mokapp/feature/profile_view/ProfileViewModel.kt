package mok.it.app.mokapp.feature.profile_view

import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.UserService

class ProfileViewModel : ViewModel() {

    val userBadgeDataInEachCategory = UserService.getBadgeSumForUserInEachCategory(
        userId = userModel.documentId,
    )
}

