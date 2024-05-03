package mok.it.app.mokapp.feature.achievement_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModel
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModelFactory

class UpdateAchievementFragment : Fragment() {
    private val args: UpdateAchievementFragmentArgs by navArgs()
    private val viewModel: EditAchievementViewModel by viewModels {
        EditAchievementViewModelFactory(args.achievement)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                UpdateAchievementScreen(viewModel)
            }
        }
}