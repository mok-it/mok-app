package mok.it.app.mokapp.feature.project_add_or_remove_members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.ui.compose.OkCancelDialog
import mok.it.app.mokapp.ui.compose.SearchField
import mok.it.app.mokapp.ui.compose.UserRow
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme

class AddOrRemoveMembersFragment : DialogFragment() {

    private val args: AddOrRemoveMembersFragmentArgs by navArgs()

    private val viewModel: AddOrRemoveMembersViewModel by viewModels {
        AddOrRemoveMembersViewModelFactory(args.projectId)
    }

    enum class DialogType {
        NONE, CONFIRM
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            MokAppTheme {
                AddOrRemoveMembersScreen(viewModel)
            }
        }
    }

    @Composable
    fun AddOrRemoveMembersScreen(viewModel: AddOrRemoveMembersViewModel) {
        val uiState by viewModel.uiState.collectAsState()

        val searchQuery by viewModel.searchQuery
        val chipState by viewModel.chipState
        val selectedUsers by viewModel.selectedUsers.observeAsState(initial = emptyList())
        val unselectedFilteredUsers by viewModel.unselectedFilteredUsers.observeAsState(initial = emptyList())

        var showDialog by rememberSaveable { mutableStateOf(DialogType.NONE) }

        Scaffold(
            bottomBar = {
                Button(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                    enabled = uiState.selectedUsersChanged,
                    onClick = {
                        showDialog = DialogType.CONFIRM
                    }) {
                    Text(text = "Mentés")
                }
            },
        ) { padding ->
            Column(
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
            ) {
                when (showDialog) {
                    DialogType.CONFIRM -> {
                        OkCancelDialog(
                            text = "Biztos, hogy el szeretnéd menteni a módosításokat? Ha valakit törölsz a projektből, az elveszíti az eddigi összes rajta megszerzett mancsot.",
                            onConfirm = {
                                viewModel.updateMembersOfProject()
                                Toast.makeText(
                                    context, "Résztvevők listája módosítva!", Toast.LENGTH_SHORT
                                ).show()
                                showDialog = DialogType.NONE
                            },
                            onDismiss = {
                                showDialog = DialogType.NONE
                            },
                        )
                    }

                    DialogType.NONE -> { /*do nothing*/
                    }
                }
                SearchField(
                    searchQuery = searchQuery,
                    chipState = chipState,
                    onValueChange = { viewModel.onSearchValueChange(it) },
                )

                LazyColumn {// selected users
                    items(selectedUsers) { user ->
                        UserRow(
                            user,
                            true,
                            findNavController(),
                        ) { _ ->
                            viewModel.userSelectionClicked(user)
                        }
                    }
                    if (unselectedFilteredUsers.isNotEmpty()) {
                        item {
                            HorizontalDivider(modifier = Modifier.padding(8.dp))
                        }
                        items(unselectedFilteredUsers) { user ->// all unselected users matching the search criteria
                            UserRow(
                                user,
                                false,
                                findNavController(),
                            ) { _ ->
                                viewModel.userSelectionClicked(user)
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Nincsenek a feltételeknek megfelelő tagok",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
