package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.compose.achievements.AchievementDetails
import mok.it.app.mokapp.fragments.viewmodels.AchievementDetailsViewModel
import mok.it.app.mokapp.fragments.viewmodels.AchievementDetailsViewModelFactory
import mok.it.app.mokapp.model.User

class AchievementDetailsFragment : Fragment() {
    private val args: AchievementDetailsFragmentArgs by navArgs()
    val viewModel: AchievementDetailsViewModel by viewModels {
        AchievementDetailsViewModelFactory(args.achievementId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val owners: List<User> by viewModel.owners.collectAsState(emptyList())
                val achievement by viewModel.achievement.collectAsState(initial = null)
                val owned by viewModel.owned.collectAsState(false)
                if (achievement == null) {
                    Text("Loading...") //TODO show loading screen
                } else {
                    AchievementDetails(
                        achievement!!,
                        owned,
                        owners,
                        viewModel
                    ) //TODO: he shot me down bang bang
                }
            }
        }
}