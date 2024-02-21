package mok.it.app.mokapp.service

import mok.it.app.mokapp.model.Project

interface IProjectService {
    fun getProjectsByIds(
        projectIds: List<String>,
        onComplete: (List<Project>) -> Unit,
        onFailure: (Exception) -> Unit
    )
}