package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.viewmodels.NotificationAdminViewModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.Searchable
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.unaccent

class NotificationAdminFragment : Fragment() {
    enum class RadioOption {
        EVERYONE {
            override fun toString() = "Mindenkinek"
        },
        EVERYONE_EXCEPT {
            override fun toString() = "Mindenkinek, kivéve:"
        },
        SPECIFIC_PEOPLE {
            override fun toString() = "Csak az alábbi embereknek:"
        },
        PROJECT_MEMBERS {
            override fun toString() = "Csak az alábbi projektek tagjainak:"
        }
    }

    private val viewModel: NotificationAdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                BasicForm()
            }
        }

    //@Preview(showSystemUi = false, showBackground = false, apiLevel = 34)
    @Composable
    fun BasicForm() {
        var showDialog by remember { mutableStateOf(false) }
        var title by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        val selectedUsers = remember { mutableStateListOf<User>() }
        val selectedProjects = remember { viewModel.selectedProjects }

        val projects by viewModel.projects.observeAsState(initial = emptyList())
        val users by viewModel.users.observeAsState(initial = emptyList())

        //TODO data is loaded, but
        //1 - - the button's text does not change for projects, only for users

        val (selectedOption, onOptionSelected) = remember { mutableStateOf(RadioOption.EVERYONE) }

        if (showDialog) {
            val usersToSendNotificationTo = viewModel.getUsersToSendNotificationTo(
                selectedOption,
                selectedUsers
            )
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        viewModel.sendNotification(
                            title,
                            text,
                            usersToSendNotificationTo
                        )
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                text = {
                    Text(
                        stringResource(
                            R.string.notification_confirmation,
                            usersToSendNotificationTo
                        )
                    )
                })
        }
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cím") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Szöveg") }
            )
            Column(
                Modifier
                    .selectableGroup()
                    .fillMaxSize()
            ) {
                RadioOption.entries.forEach { radioOption ->
                    RadioButtonRow(
                        selectedOption = selectedOption,
                        radioOption = radioOption,
                        onOptionSelected = onOptionSelected
                    )
                }
                // if the selected option deals with users
                if (selectedOption == RadioOption.EVERYONE_EXCEPT
                    || selectedOption == RadioOption.SPECIFIC_PEOPLE
                ) {
                    if (users.isNotEmpty()) {
                        MultiSelectList(
                            users as List<Searchable>,
                            selectedUsers.toMutableSet()
                        ) { item ->
                            if (selectedUsers.contains(item)) {
                                selectedUsers.remove(item)
                            } else {
                                selectedUsers.add(item as User)
                            }
                        }
                    } else {
                        Text("Nincs elérhető felhasználó")
                    }
                } else if (selectedOption == RadioOption.PROJECT_MEMBERS) {
                    if (projects.isNotEmpty()) {
                        MultiSelectList(
                            projects as List<Searchable>,
                            selectedProjects.toMutableSet()
                        ) { item ->
                            if (viewModel.selectedProjects.contains(item)) {
                                viewModel.selectedProjects.remove(item)
                            } else {
                                viewModel.selectedProjects.add(item as Project)
                            }
                            Log.d(
                                "NotificationAdminFragment",
                                "selectedProjects: ${viewModel.selectedProjects.count()}"
                            )
                        }
                    } else {
                        Text("Nincs elérhető projekt")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        //TODO check the validness of the form fields, i.e. title and text are not empty

                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                ) {
                    Text(
                        "Küldés ennyi embernek: ${
                            viewModel.getUsersToSendNotificationTo(
                                selectedOption,
                                selectedUsers
                            ).size
                            //TODO ez nem livedata, ezért biztosan nem fog frissülni a szám, amíg nincs recomposition
                            // -> mindig, amikor a viewModel.users VAGY a selectedProjects változik, ennek is újra kéne hívódnia (ezért lenne jó a livedata)
                        }"
                    )
                }
            }
        }

    }

    @Composable
    fun <T : Searchable> MultiSelectList(
        items: List<T>,
        selectedItems: MutableSet<T>,
        onItemSelected: (T) -> Unit
    ) {
        var searchQuery by remember { mutableStateOf("") }

        val filteredUsers = items.filter { item ->
            item.name.unaccent().contains(searchQuery.trim().unaccent(), ignoreCase = true)
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(56.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Keresés") },
                    modifier = Modifier
                        .weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                )
            }
            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
            ) {
                items(filteredUsers) { item ->
                    RadioButtonRow(onItemSelected, item, selectedItems)
                }
            }
        }
    }

    @Composable
    private fun <T : Searchable> RadioButtonRow(
        onItemSelected: (T) -> Unit,
        item: T,
        selectedItems: MutableSet<T>
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable
                {
                    onItemSelected(item)
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selectedItems.contains(item),
                onCheckedChange = null // Accessibility: 'selectable' handles state
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    @Composable
    fun RadioButtonRow(
        selectedOption: RadioOption,
        radioOption: RadioOption,
        onOptionSelected: (RadioOption) -> Unit
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .selectable(
                    selected = selectedOption == radioOption,
                    onClick = { onOptionSelected(radioOption) },
                    role = Role.RadioButton
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption == radioOption,
                onClick = null // null recommended for accessibility with screenreaders
            )
            Text(
                text = radioOption.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}