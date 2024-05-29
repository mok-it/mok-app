package mok.it.app.mokapp.feature.achievement_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.LoadingScreen
import mok.it.app.mokapp.ui.compose.achievements.AchievementDetails
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.utility.Utility.TAG
import java.util.SortedMap

class AchievementDetailsFragment : Fragment() {
    private val args: AchievementDetailsFragmentArgs by navArgs()
    val viewModel: AchievementDetailsViewModel by viewModels {
        AchievementDetailsViewModelFactory(args.achievementId)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MokAppTheme {
                    val owners: SortedMap<Int, List<User>> by viewModel.owners.collectAsState(
                            sortedMapOf()
                    )
                    val achievement by viewModel.achievement.collectAsState(initial = null)
                    Surface {

                        achievement?.let { achievement ->
                            val achievementModel by viewModel.achievementModel.collectAsState(null)
                            Column {
                                AchievementDetails(
                                        achievement,
                                        owners,
                                        viewModel.isUserAdmin,
                                        navController = findNavController(),
                                        onEditClick = {
                                            achievementModel?.let {
                                                findNavController().navigate(
                                                        AchievementDetailsFragmentDirections.actionAchievementDetailsFragmentToUpdateAchievementFragment(
                                                                it
                                                        )
                                                )
                                            } ?: run { handleNullAchievement() }
                                        },
                                        onGrantClick = {
                                            achievementModel?.let {
                                                findNavController().navigate(
                                                        AchievementDetailsFragmentDirections.actionAchievementDetailsFragmentToGrantAchievementFragment(
                                                                it.id
                                                        )
                                                )
                                            } ?: run { handleNullAchievement() }
                                        }
                                )
                            }
                        } ?: run { LoadingScreen() }
                    }
                }
            }
        }
    }

    private fun handleNullAchievement() {
        Toast.makeText(
                context,
                "Hiba történt. Kérlek próbáld újra később.",
                Toast.LENGTH_SHORT
        ).show()
        Log.wtf(TAG, "Achievement model is null")
    }
}
