package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement

class AchievementListViewModel : ViewModel() {
    private val _achievements: MutableStateFlow<List<Achievement>> = MutableStateFlow(listOf())
    val achievements: StateFlow<List<Achievement>> get() = _achievements

    init {
        fetchAchievements()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchAchievements() {
        viewModelScope.launch {
            AchievementService.getAchievements().collect { achievements ->
                _achievements.value = achievements
            }
        }
    }
}