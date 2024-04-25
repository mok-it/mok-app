package mok.it.app.mokapp.feature.project_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project

class AllProjectsListViewModel : ViewModel() {
    val projects: LiveData<List<Project>> = ProjectService.getAllProjects()
}
