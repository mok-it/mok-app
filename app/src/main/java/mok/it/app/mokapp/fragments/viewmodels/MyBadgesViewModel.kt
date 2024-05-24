package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.enums.Category

class MyBadgesViewModel : ViewModel() {

    val myProjects = ProjectService.getProjectsByIds(userModel.projectBadges.keys.toList())

    val myCategories: LiveData<List<Category>> = myProjects.map { projects ->
        projects.map { it.categoryEnum }.distinct()
    }

    fun projectsInCategory(
        category: Category,
    ) = myProjects.value?.filter { it.categoryEnum == category } ?: emptyList()
}
