package mok.it.app.mokapp.feature.achievement_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import mok.it.app.mokapp.compose.achievements.AchievementCard
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.enums.Role

class AchievementsFragment : Fragment() {
    private val viewModel: AchievementListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val achievements by viewModel.achievements.collectAsState(emptyList())
                Scaffold(
                    floatingActionButton = {
                        if (FirebaseUserObject.userModel.roleAtLeast(Role.ADMIN)) {
                            FloatingActionButton(
                                onClick = {
                                    findNavController().navigate(
                                        AchievementsFragmentDirections.actionAchievementsFragmentToCreateAchievementFragment()
                                    )
                                },
                                modifier = Modifier
                                    .padding(16.dp),
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Acsi Létrehozása")
                            }
                        }
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        LazyColumn {
                            items(achievements) { achievement ->
                                AchievementCard(
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
}