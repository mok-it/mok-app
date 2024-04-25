package mok.it.app.mokapp.feature.member_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project

class MemberViewModel : ViewModel() {
    fun getProjectsByIds(mutualProjectIds: List<String>): LiveData<List<Project>> =
        ProjectService.getProjectsByIds(mutualProjectIds)
}
