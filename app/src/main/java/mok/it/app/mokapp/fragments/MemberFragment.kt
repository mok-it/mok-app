package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.composables.UserCard
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.viewmodels.MemberViewModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

class MemberFragment : Fragment() {

    private val args: MemberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                MembersScreen()
            }
        }

    @Composable
    private fun MembersScreen() {
        val viewModel: MemberViewModel by viewModels()

        Column {
            UserCard(args.user)
            MutualProjectsOfUsers(userModel, args.user, viewModel) {
                findNavController().navigate(
                    MemberFragmentDirections.actionMemberFragmentToDetailsFragment(
                        it.id
                    )
                )
            }
        }
    }

    @Composable
    private fun MutualProjectsOfUsers(
        thisUser: User,
        otherUser: User,
        viewModel: MemberViewModel,
        onProjectClick: (Project) -> Unit
    ) {
        val mutualProjectIds =
            thisUser.joinedBadges.intersect(otherUser.joinedBadges.toSet()).toList()

        val mutualProjects by viewModel.getProjectsByIds(mutualProjectIds).observeAsState(listOf())

        Column {
            Text(
                text = "Közös projektjeitek",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (mutualProjects.isEmpty()) {
                Text("Betöltés...")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mutualProjects) { project ->
                        ProjectCard(project, onProjectClick)
                    }
                }
            }
        }
    }

    @Composable
    private fun ProjectCard(project: Project, onClick: (Project) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { onClick(project) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}