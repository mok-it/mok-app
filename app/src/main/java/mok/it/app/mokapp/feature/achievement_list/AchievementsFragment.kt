package mok.it.app.mokapp.feature.achievement_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import mok.it.app.mokapp.compose.achievements.AchievementCard

class AchievementsFragment : Fragment() {
    private val viewModel: AchievementListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val achievements by viewModel.achievements.collectAsState(emptyList())
                Column {
                    LazyColumn {
                        items(achievements) { achievement ->
//                            val owned by viewModel.isOwned(achievement).collectAsState(false)
                            AchievementCard(
//                                owned = owned,
                                achievement = achievement,
                                onClick = {
                                    findNavController().navigate(
                                        AchievementsFragmentDirections.actionAchievementsFragmentToAchievementDetailsFragment(
                                            achievement.id
                                        )
                                    )
                                })
                        }
                    }
                }
            }
        }
}