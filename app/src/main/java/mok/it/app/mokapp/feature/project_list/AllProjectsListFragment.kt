//new
package mok.it.app.mokapp.feature.project_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModelFlow
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.ui.compose.SearchField
import mok.it.app.mokapp.ui.compose.projects.ProjectCard
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme

class AllProjectsListFragment : Fragment() {

    private val args: AllProjectsListFragmentArgs by navArgs()
    private val viewModel: AllProjectsListViewModel by viewModels()

    //filteringhez:
    //    var achieved: Boolean = false,
    //    var joined: Boolean = false,
    //    var edited: Boolean = false,

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            lifecycleScope.launch { setupTopMenu() }
            loginOrLoad {
                setContent {
                    val searchQuery by viewModel.searchQuery
                    val chipState by viewModel.chipState
                    val filteredProjects by viewModel.filteredProjects.collectAsState(initial = emptyList())
                    MokAppTheme {
                        AllProjectsListScreen(
                                searchQuery = searchQuery,
                                chipState = chipState,
                                filteredProjects = filteredProjects,
                                onCreateProject = {
                                    findNavController().navigate(
                                            AllProjectsListFragmentDirections.actionAllProjectsListFragmentToCreateProjectFragment(
                                                    args.category
                                            )
                                    )
                                },
                                onSearchValueChange = { viewModel.onSearchValueChange(it) },
                                onNavigateToProject = { project ->
                                    val action =
                                            AllProjectsListFragmentDirections.actionAllProjectsListFragmentToDetailsFragment(
                                                    project.id
                                            )
                                    findNavController().navigate(action)
                                }
                        )
                    }
                }
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

    private suspend fun setupTopMenu() {
        if (userModelFlow.firstOrNull()?.roleAtLeast(Role.AREA_MANAGER) != true) {
            return
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(R.id.menu, R.id.menu, 0, R.string.delete)
                        .setIcon(R.drawable.ic_three_dots)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                @Suppress("DEPRECATION")
                menu.getItem(0).icon?.mutate()
                        ?.setTint(resources.getColor(R.color.md_theme_onPrimary))
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu -> {
                        findNavController().navigate(AllProjectsListFragmentDirections.actionAllProjectsListFragmentToProjectImportExportFragment())

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}

@Composable
fun AllProjectsListScreen(
        searchQuery: String,
        chipState: ChipTextFieldState<Chip>,
        filteredProjects: List<Project>,
        onCreateProject: () -> Unit,
        onSearchValueChange: (String) -> Unit,
        onNavigateToProject: (Project) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Scaffold(floatingActionButton = {
        val user by userModelFlow.collectAsState(initial = User())
        if (user.roleAtLeast(Role.AREA_MANAGER)) {
            FloatingActionButton(
                    onClick = onCreateProject,
                    modifier = Modifier
                            .padding(16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Project")
            }
        }
    }) { padding ->
        Surface {
            Column {
                SearchField(
                        searchQuery = searchQuery,
                        chipState = chipState,
                        onValueChange = onSearchValueChange,
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
                            ProjectCard(
                                    project = project, onClick = onNavigateToProject
                            )
                        }
                    }
                }
            }
        }
    }
}