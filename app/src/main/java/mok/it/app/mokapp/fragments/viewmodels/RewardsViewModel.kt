package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import mok.it.app.mokapp.firebase.service.RewardsService
import mok.it.app.mokapp.model.Reward

class RewardsViewModel : ViewModel() {
    fun updateReward(reward: Reward) {
        RewardsService.updateReward(reward)
    }

    fun deleteReward(reward: Reward) {
        RewardsService.deleteReward(reward)
    }

    fun requestReward(reward: Reward) {
        RewardsService.requestReward(reward)
    }

    val rewards: StateFlow<List<Reward>> = RewardsService.getAllRewards()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
