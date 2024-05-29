package mok.it.app.mokapp.feature.my_badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.ProjectService

class MyBadgesViewModel : ViewModel() {

    val projectsByIds =
            ProjectService.getProjectsByIds(userModel.projectBadges.keys.toList()).asLiveData()
}
