package mok.it.app.mokapp.feature.member_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.UserCard

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
            thisUser.projectBadges.keys.intersect(otherUser.projectBadges.keys.toSet()).toList()

        val mutualProjects by viewModel.getProjectsByIds(mutualProjectIds).observeAsState(listOf())

        Column {
            Text(
                text = "Közös projektjeitek",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (mutualProjects.isEmpty()) {
                Text("Nincsenek közös projektjeitek :(", modifier = Modifier.padding(16.dp))
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
            Row(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = project.icon,
                    contentDescription = "Project icon",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15))
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Inside
                )
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}