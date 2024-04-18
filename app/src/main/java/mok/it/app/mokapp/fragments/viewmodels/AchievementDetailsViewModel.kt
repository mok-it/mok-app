package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.AchievementService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User

class AchievementDetailsViewModel(private val achievementId: String) : ViewModel() {
    private val _owners: MediatorLiveData<List<User>> = MediatorLiveData(emptyList())
    public val owners: LiveData<List<User>> get() = _owners
    private val _achievement: MediatorLiveData<Achievement> = MediatorLiveData()
    public val achievement: LiveData<Achievement> get() = _achievement
    public val owned: LiveData<Boolean>
        get() = MutableLiveData(
            userModel.achievements.contains(
                achievementId
            )
        )

    init {
        fetchAchievementData()
//        fetchOwnerData()
    }

    private fun fetchAchievementData() {
        viewModelScope.launch {
            val achievementLiveData = AchievementService.getAchievement(achievementId)
            _achievement.addSource(achievementLiveData) {
                _achievement.value = it
                fetchOwnerData()
            }
        }
    }

    private fun fetchOwnerData() {
        viewModelScope.launch {
            _achievement.observeForever { achievement ->
                val ownersLiveData = UserService.getUsers(achievement.ownerIds)
                _owners.addSource(ownersLiveData) {
                    _owners.value = it
                }
            }
        }
    }
}

class AchievementDetailsViewModelFactory(private val achievementId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementDetailsViewModel::class.java)) {
            return AchievementDetailsViewModel(achievementId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
