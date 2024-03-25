package mok.it.app.mokapp.service

import androidx.lifecycle.LiveData
import mok.it.app.mokapp.model.Project

interface IProjectService {
    fun getProjectsByIds(
        projectIds: List<String>,
        onComplete: (List<Project>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getProjectsByIds(
        projectIds: List<String>
    ): LiveData<List<Project>>
}