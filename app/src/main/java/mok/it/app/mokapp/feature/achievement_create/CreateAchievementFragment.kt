package mok.it.app.mokapp.feature.achievement_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.model.Achievement

class CreateAchievementFragment : Fragment() {
    private val viewModel: CreateAchievementViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val achievement = remember {
                    mutableStateOf(Achievement(levelDescriptions = sortedMapOf(1 to "")))
                }
                CreateAchievementScreen(achievement)
            }
        }
}
