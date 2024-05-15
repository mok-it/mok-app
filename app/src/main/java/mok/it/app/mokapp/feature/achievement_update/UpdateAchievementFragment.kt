package mok.it.app.mokapp.feature.achievement_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R
import mok.it.app.mokapp.feature.achievement_create.EditAchievementEvent
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
    ): View {
        setupTopMenu()
        return ComposeView(requireContext()).apply {
            setContent {
                UpdateAchievementScreen(viewModel) { findNavController().popBackStack() }
            }
        }
    }

    private fun setupTopMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(R.id.delete, R.id.delete, 0, R.string.delete)
                    .setIcon(R.drawable.ic_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.delete -> {
                        viewModel.onEvent(EditAchievementEvent.Delete)
                        findNavController().popBackStack(
                            destinationId = R.id.achievementsFragment,
                            inclusive = false
                        )

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}