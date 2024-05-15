package mok.it.app.mokapp.feature.achievement_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class CreateAchievementFragment : Fragment() {
    private val viewModel: EditAchievementViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                CreateAchievementScreen(
                    viewModel,
                    onNavigateBack = { findNavController().popBackStack() })
            }
        }
}
