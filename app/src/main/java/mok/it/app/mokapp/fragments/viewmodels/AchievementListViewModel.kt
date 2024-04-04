package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.model.Achievement

class AchievementListViewModel : ViewModel() {
    private val _achievements: MediatorLiveData<List<Achievement>> = MediatorLiveData(listOf())
    val achievements: LiveData<List<Achievement>> get() = _achievements

    init {
        fetchAchievements()
    }

    private fun fetchAchievements() {
        viewModelScope.launch {
            val achievementsLiveData = AchievementService.getAchievements()
            _achievements.addSource(achievementsLiveData) {
                _achievements.value = it
            }
        }
    }
}