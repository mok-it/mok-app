package mok.it.app.mokapp.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.compose.RewardItem
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.RewardsService
import mok.it.app.mokapp.fragments.viewmodels.RewardsViewModel
import mok.it.app.mokapp.model.Reward

class RewardsFragment : Fragment() {
    //TODO
    //2 - meghatározni, mit szeretne egy admin csinálni (de sztem: admin mód switch(?),
    // és ha be van kapcsolva, akk ceruza ikonra nyomva editálhatóak az árak és a mennyiségek is,
    // és a jutalmakat törölni is lehet)
    //3 - a fentieket implementálni

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                RewardsScreen()
            }
        }

    @Composable
    private fun RewardsScreen() {
        val viewModel: RewardsViewModel by viewModels()
        val rewards = viewModel.rewards.collectAsState(initial = emptyList())

        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Mancsaid száma",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    BadgeIcon(
                        badgeNumber = userModel.points.toString(),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            LazyColumn {
                items(rewards.value) { reward ->
                    RewardItem(reward, ::canBeBoughtByCurrentUser, ::requestReward)
                }
            }
        }
    }

    private fun canBeBoughtByCurrentUser(reward: Reward) =
        userModel.points >= reward.price
                && reward.quantity > 0
                && !userModel.requestedRewards.contains(reward.documentId)

    private fun requestReward(reward: Reward) {
        (activity as Activity).let {
            MaterialDialog.Builder(it)
                .setTitle("Biztosan kéred a jutalmat?")
                .setMessage("${reward.price} mancs kerül majd levonásra tőled.")
                .setPositiveButton(
                    it.getString(R.string.ok), R.drawable.ic_check
                ) { dialogInterface, _ ->
                    RewardsService.acceptRewardRequest(reward) {
                        //TODO
                    }
                    dialogInterface.dismiss()
                }
                .setNegativeButton(
                    "Mégsem", R.drawable.ic_close_24
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .build()
                .show()
        }
    }
}