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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.rememberChipTextFieldState
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.ui.compose.SearchField
import mok.it.app.mokapp.ui.compose.projects.ProjectCard
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.utility.Utility.unaccent

class AllProjectsListFragment : Fragment() {

    private val args: AllProjectsListFragmentArgs by navArgs()
    private val viewModel: AllProjectsListViewModel by viewModels()

    //filteringhez:
    //    var achieved: Boolean = false,
    //    var joined: Boolean = false,
    //    var edited: Boolean = false,

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupTopMenu()
        return ComposeView(requireContext()).apply {
            loginOrLoad {
                setContent {
                    MokAppTheme {
                        AllProjectsListScreen()
                    }
                }
            }
        }
    }

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
            Surface {
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
                                ProjectCard(project = project, onClick = {
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


    private fun setupTopMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(R.id.menu, R.id.menu, 0, R.string.delete)
                    .setIcon(R.drawable.ic_three_dots)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
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