package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.rememberChipTextFieldState
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.compose.SearchField
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.viewmodels.AllProjectsListViewModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.utility.Utility.unaccent

class AllProjectsListFragment : Fragment() {

    private val args: AllProjectsListFragmentArgs by navArgs()
    private val viewModel: AllProjectsListViewModel by viewModels()

    //filteringhez:
    //    var achieved: Boolean = false,
    //    var joined: Boolean = false,
    //    var edited: Boolean = false,

    @Composable
    fun AllProjectsListScreen() {
        val lazyListState = rememberLazyListState()

        val chipState = rememberChipTextFieldState<Chip>()
        var searchQuery by remember { mutableStateOf("") }
        val filteredProjects = getFilteredProjects(searchQuery, chipState)

        Scaffold(floatingActionButton = {
            if (userModel.roleAtLeast(Role.AREA_MANAGER)) {
                FloatingActionButton(
                    onClick = {
                        findNavController().navigate(
                            AllProjectsListFragmentDirections.actionAllProjectsListFragmentToCreateProjectFragment(
                                args.category
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Project")
                }
            }
        }) { padding ->
            Column {
                SearchField(
                    searchQuery = searchQuery,
                    chipState = chipState,
                    onValueChange = { searchQuery = it },
                )
                if (filteredProjects.isEmpty()) {
                    Text(
                        text = "Nincsenek a feltételeknek megfelelő projektek",
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    LazyColumn(state = lazyListState) {
                        items(filteredProjects) { project ->
                            ProjectItem(project = project, onClick = {
                                val action =
                                    AllProjectsListFragmentDirections.actionAllProjectsListFragmentToDetailsFragment(
                                        project.id
                                    )
                                findNavController().navigate(action)
                            })
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun getFilteredProjects(
        searchQuery: String,
        chipState: ChipTextFieldState<Chip>
    ) = viewModel.projects.observeAsState().value
        ?.filter { project -> isProjectMatched(project, searchQuery, chipState) }
        .orEmpty()
        .sortedWith(compareBy({ it.categoryEnum }, { it.name }))

    private fun isProjectMatched(
        project: Project,
        cleanSearchQuery: String,
        chipState: ChipTextFieldState<Chip>
    ): Boolean {
        val cleanSearchWords =
            chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        return cleanSearchWords.all {
            project.name.unaccent().contains(it, ignoreCase = true)
                    || project.description.unaccent().contains(it, ignoreCase = true)
                    || project.categoryEnum.toString().contains(it, ignoreCase = true)
                    || it == project.maxBadges.toString()
        }
    }

    @Composable
    fun ProjectItem(project: Project, onClick: (Project) -> Unit) {
        Card(
            onClick = { onClick(project) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardColors(
                containerColor = if (userModel.projectBadges.contains(project.id))
                    Color(0xFF00FF00)
                else
                    CardDefaults.cardColors().containerColor,
                contentColor = CardDefaults.cardColors().contentColor,
                disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
                disabledContentColor = CardDefaults.cardColors().disabledContentColor,
            )
        ) {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = project.icon,
                    contentDescription = "Project icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BadgeIcon(
                    badgeNumberText = project.maxBadges.toString(),
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        loginOrLoad {
            setContent {
                AllProjectsListScreen()
            }
        }
    }

    private fun loginOrLoad(setComposeContent: () -> Unit) {
        if (currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            refreshCurrentUserAndUserModel(requireContext()) {
                setComposeContent()
            }
        }
    }
}