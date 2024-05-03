package mok.it.app.mokapp.feature.achievement_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.compose.achievements.AchievementDetails
import mok.it.app.mokapp.model.User
import java.util.SortedMap

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
                val owners: SortedMap<Int, List<User>> by viewModel.owners.collectAsState(
                    sortedMapOf()
                )
                val achievement by viewModel.achievement.collectAsState(initial = null)
                if (achievement == null) {
                    Text("Loading...") //TODO show loading screen
                } else {
                    val achievementModel by
                    viewModel.achievementModel.collectAsState(null)
                    Column { //TODO: delete
                        Button(
                            onClick = {
                                if (achievementModel != null) {
                                    findNavController().navigate(
                                        AchievementDetailsFragmentDirections.actionAchievementDetailsFragmentToUpdateAchievementFragment(
                                            achievementModel!! //TODO he shot me down bang bang
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "A betöltés folyamatban...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Text(text = "Módosítás")
                        }
                        AchievementDetails(
                            achievement!!,
                            owners,
                            viewModel
                        ) //TODO: he shot me down bang bang
                    }
                }
            }
        }
}