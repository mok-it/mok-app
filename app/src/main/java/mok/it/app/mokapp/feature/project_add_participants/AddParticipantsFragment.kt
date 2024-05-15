package mok.it.app.mokapp.feature.project_add_participants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.rememberChipTextFieldState
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.SearchField
import mok.it.app.mokapp.ui.compose.UserIcon
import mok.it.app.mokapp.utility.Utility.unaccent

class AddParticipantsFragment : DialogFragment() {

    private val args: AddParticipantsFragmentArgs by navArgs()

    private val viewModel: AddParticipantsViewModel by viewModels {
        AddParticipantsViewModelFactory(args.projectId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AddParticipantsScreen(viewModel)
        }
    }

    @Composable
    fun AddParticipantsScreen(viewModel: AddParticipantsViewModel) {
        val uiState by viewModel.uiState.collectAsState()

        val chipState = rememberChipTextFieldState<Chip>()
        var searchQuery by remember { mutableStateOf("") }
        val filteredUsers = getFilteredUsers(searchQuery, chipState, uiState.selectedUsers)

        Scaffold(
            bottomBar = {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    enabled = uiState.selectedUsersChanged, onClick = {
                        viewModel.setMembersOfProject()
                        Toast.makeText(context, "Résztvevők listája módosítva!", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                    Text(text = "Mentés")
                }
            },
        ) { padding ->
            Column(
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
            ) {
                SearchField(
                    searchQuery = searchQuery,
                    chipState = chipState,
                    onValueChange = { searchQuery = it },
                )

                if (filteredUsers.isNotEmpty()) {
                    LazyColumn {
                        items(filteredUsers) { user ->
                            UserRow(
                                user,
                                uiState.selectedUsers.contains(user.documentId),
                            ) { _ ->
                                viewModel.userSelectionClicked(user)
                            }
                        }
                    }
                } else {
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

    @Composable
    fun UserRow(
        user: User,
        isSelected: Boolean,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable
                {
                    onCheckedChange(!isSelected)
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserIcon(user = user, navController = findNavController())
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
            Checkbox(
                checked = isSelected,
                onCheckedChange = null // Accessibility: 'selectable' handles state
            )
        }
    }

    @Composable
    private fun getFilteredUsers(
        searchQuery: String,
        chipState: ChipTextFieldState<Chip>,
        selectedUserIds: List<String>,
    ) = viewModel.users.observeAsState().value?.filter { user ->
        isUserMatched(
            user, searchQuery, chipState
        )
    }.orEmpty()
        // we put selected users first, then alphabetically
        .sortedWith(compareBy({ !selectedUserIds.contains(it.documentId) }, { it.name }))

    private fun isUserMatched(
        user: User,
        cleanSearchQuery: String,
        chipState: ChipTextFieldState<Chip>,
    ): Boolean {
        val cleanSearchWords =
            chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        // users are searchable by name or nickname
        return cleanSearchWords.all {
            user.name.unaccent().contains(it, ignoreCase = true) || user.nickname.unaccent()
                .contains(it, ignoreCase = true)
        }
    }

}
