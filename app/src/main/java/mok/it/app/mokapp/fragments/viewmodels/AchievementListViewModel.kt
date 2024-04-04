package mok.it.app.mokapp.fragments.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement

class AchievementListViewModel: ViewModel() {
    private val _achievements : MutableStateFlow<List<Achievement>> = MutableStateFlow(listOf())
    val achievements: StateFlow<List<Achievement>> get() = _achievements

    init {
        fetchAchievements()
//        _achievements.value = listOf(
//            Achievement("1", "Achievement 1", "Description 1", "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef", false),
//            Achievement("2", "Achievement 2", "Description 2", "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef", true))
    }

    public fun fetchAchievements() {
        viewModelScope.launch {
            AchievementService.getAchievements().collect {achievements ->
                _achievements.value = achievements
            }
        }
    }

    public fun addAchievement() {
        _achievements.value += Achievement("3", "Achievement 3", "Description 3", "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef", false)
    }
}