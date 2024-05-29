package mok.it.app.mokapp.feature.project_import_export

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.ExportProject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.toExportProject
import java.util.Date

class ProjectImportExportViewModel : ViewModel() {

    val importedProjects = mutableStateListOf<Project>()
    val importError = mutableStateOf(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val existingProjects: Flow<List<ExportProject>>
        get() {
            return ProjectService.getAllProjects().flatMapLatest { projects ->
                val xd = projects.map { project ->
                    UserService.getUsers(project.members).map { users ->
                        project.toExportProject(users)
                    }
                }
                combine(xd) { it.toList() }
            }
        }


    fun saveImport() {
        importedProjects.forEach { project ->
            ProjectService.addProject(
                    project.copy(
                            created = Date(),
                            creator = userModel.documentId,
                            projectLeader = userModel.documentId,
                            overallProgress = 0
                    )
            )
        }
    }

    fun selectImport(projects: List<Project>?) {
        importedProjects.clear()
        if (projects != null) {
            importError.value = false
            importedProjects.addAll(projects)
        } else {
            importError.value = true
            Log.w("ProjectImportExport", "Imported projects are null")
        }
    }
}
