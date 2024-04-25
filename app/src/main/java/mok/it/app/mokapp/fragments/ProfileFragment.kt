package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.compose.ProjectBadgeSummary
import mok.it.app.mokapp.compose.UserCard
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen()
            }
        }

    @Composable
    fun ProfileScreen() {
        val viewModel: ProfileViewModel by viewModels()
        Column {
            UserCard(
                user = userModel
            )
            ProjectBadgeSummary(viewModel)
        }
    }
}