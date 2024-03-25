package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.ProjectService

class MyBadgesViewModel : ViewModel() {

    val projectsByIds = ProjectService.getProjectsByIds(userModel.projectBadges.keys.toList())
}
