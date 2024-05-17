package mok.it.app.mokapp.feature.project_list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.utility.Utility.unaccent

class AllProjectsListViewModel : ViewModel() {
    private val _projects: Flow<List<Project>> = ProjectService.getAllProjects()
    val filteredProjects
        get() = _projects.map { projects ->
            projects.filter { project ->
                isProjectMatched(
                    project,
                    searchQuery.value,
                    chipState.value
                )
            }
                .sortedWith(
                    compareBy({ it.categoryEnum }, { it.name })
                )
        }
    private val _searchQuery = mutableStateOf("")
    val searchQuery get() = _searchQuery
    private val _chipState = mutableStateOf(ChipTextFieldState<Chip>())
    val chipState get() = _chipState

    private fun isProjectMatched(
        project: Project,
        cleanSearchQuery: String,
        chipState: ChipTextFieldState<Chip>
    ): Boolean {
        val cleanSearchWords =
            chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        return cleanSearchWords.all {
            project.name.unaccent().contains(it, ignoreCase = true)
                    || project.description.unaccent().contains(it, ignoreCase = true)
                    || project.categoryEnum.toString().contains(it, ignoreCase = true)
                    || it == project.maxBadges.toString()
        }
    }

    fun onSearchValueChange(value: String) {
        _searchQuery.value = value
    }
}