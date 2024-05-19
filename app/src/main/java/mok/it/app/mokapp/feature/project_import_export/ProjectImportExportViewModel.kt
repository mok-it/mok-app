package mok.it.app.mokapp.feature.project_import_export

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.ExportProject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.toExportProject

class ProjectImportExportViewModel : ViewModel() {

    val importedProjects = mutableStateListOf<Project>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val existingProjects: Flow<List<ExportProject>>
        get() {
            return ProjectService.getProjects().flatMapLatest { projects ->
                val xd = projects.map { project ->
                    UserService.getUsers(project.members).map { users ->
                        project.toExportProject(users)
                    }
                }
                combine(xd) { it.toList() }
            }
        }


    fun onEvent(event: ImportExportEvent) {
        when (event) {
            is ImportExportEvent.Import -> {
                importedProjects.clear()
                importedProjects.addAll(event.projects)
            }

            is ImportExportEvent.Export -> {
            }
        }
    }

}

sealed class ImportExportEvent {
    data class Import(val projects: List<Project>) : ImportExportEvent()
    data object Export : ImportExportEvent()
}