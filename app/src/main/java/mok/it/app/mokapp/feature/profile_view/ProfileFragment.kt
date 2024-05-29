package mok.it.app.mokapp.feature.profile_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.ui.compose.ProjectBadgeSummary
import mok.it.app.mokapp.ui.compose.UserCard
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme

class ProfileFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View =
            ComposeView(requireContext()).apply {
                setContent {
                    MokAppTheme {
                        ProfileScreen()
                    }
                }
            }

    @Composable
    fun ProfileScreen() {
        val viewModel: ProfileViewModel by viewModels()
        Surface {
            Column {
                UserCard(
                        user = userModel,
                        navController = findNavController(),
                        enableOnClick = false,
                )
                ProjectBadgeSummary(viewModel)
            }
        }
    }
}