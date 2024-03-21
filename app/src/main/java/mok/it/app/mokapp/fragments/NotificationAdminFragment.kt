package mok.it.app.mokapp.fragments

import android.app.Activity
import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.viewmodels.NotificationAdminViewModel
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.Searchable
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.unaccent

class NotificationAdminFragment : Fragment() {
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

    @Preview(showSystemUi = false, showBackground = false, apiLevel = 34)
    @Composable
    fun BasicForm() {
        var title by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        val users = remember {
            viewModel.users
        }
        val projects = remember {
            viewModel.projects
        }
        val selectedUsers = remember { mutableSetOf<User>() }
        val selectedProjects = remember { mutableSetOf<Project>() }
        val radioOptions =
            listOf(
                "Mindenkinek",
                "Mindenkinek, kivéve:",
                "Csak az alábbi embereknek:",
                "Csak az alábbi projektek tagjainak:"
            )
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

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
                radioOptions.forEach { text ->
                    RadioButtonRow(
                        selectedOption = selectedOption,
                        text = text,
                        onOptionSelected = onOptionSelected
                    )
                }
                // if the selected option deals with users
                if (selectedOption == radioOptions[1] || selectedOption == radioOptions[2]) {
                    if (users.value != null) {
                        MultiSelectList(
                            users.value!! as List<Searchable>,
                            selectedUsers as MutableSet<Searchable>
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
                } else if (selectedOption == radioOptions[3]) {
                    if (projects.value != null) {
                        MultiSelectList(
                            projects.value!! as List<Searchable>,
                            selectedProjects as MutableSet<Searchable>
                        ) { item ->
                            if (selectedProjects.contains(item)) {
                                selectedProjects.remove(item)
                            } else {
                                selectedProjects.add(item as Project)
                            }
                        }
                    } else {
                        Text("Nincs elérhető projekt")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        //TODO check the validness of the form fields, i.e. title and text are not empty

                        val usersToSendNotificationTo = viewModel.getUsersToSendNotificationTo(
                            selectedOption,
                            radioOptions,
                            users,
                            selectedUsers,
                            selectedProjects
                        )
                        (context as Activity).let {
                            MaterialDialog.Builder(it)
                                .setTitle(
                                    it.getString(
                                        R.string.notification_confirmation,
                                        usersToSendNotificationTo.size
                                    )
                                )
                                .setNegativeButton(it.getString(R.string.discard)) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                                .setPositiveButton(it.getString(R.string.ok)) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    viewModel.sendNotification(
                                        title,
                                        text,
                                        usersToSendNotificationTo
                                    )
                                }
                                .build()
                                .show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                ) {
                    Text(
                        "Küldés ennyi embernek: ${
                            viewModel.getUsersToSendNotificationTo(
                                selectedOption,
                                radioOptions,
                                users,
                                selectedUsers,
                                selectedProjects
                            ).size
                        }"
                    )
                }
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
        }
    }
}

@Composable
fun RadioButtonRow(
    selectedOption: String,
    text: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selectedOption == text,
                onClick = { onOptionSelected(text) },
                role = Role.RadioButton
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedOption == text,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}