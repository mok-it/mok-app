package mok.it.app.mokapp.fragments

import DetailsViewModel
import DetailsViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.compose.DataBlock
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.utility.Utility.TAG
import java.text.DateFormat

class DetailsFragment : Fragment() {

    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModelFactory(args.projectId)
    }

    enum class DialogType {
        NONE, JOIN, LEAVE, MEMBERS
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupTopMenu()
        return ComposeView(requireContext()).apply {
            setContent {
                DetailsScreen()
            }
        }
    }

    //TODO nem frissül sem a projectmembers, sem pedig a gomb szövege (de a db-ben igen azért) - ez is a FirebaseUserObject refaktorálásától függ (többek közt)
    // ha a FirebaseUserObject refaktorálva lesz, ez a függvény is sokat egyszerűsödik majd

    @Composable
    private fun DetailsScreen() {
        val isPreview = LocalInspectionMode.current

        val project by viewModel.project.observeAsState(initial = Project())
        val creator by viewModel.creatorUser.observeAsState(initial = User())
        var showDialog by remember { mutableStateOf(DialogType.NONE) }
        val members =
            if (LocalInspectionMode.current) {
                listOf(
                    User(name = "Teszt Ödön"),
                    User(name = "Teszt József"),
                    User(name = "Teszt Béla"),
                    User(name = "Teszt Jenő"),
                    User(name = "Teszt József"),
                    User(name = "Teszt Béla")
                )
            } else {
                viewModel.members.observeAsState(initial = emptyList()).value
            }

        Scaffold(
            bottomBar = {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {

                        showDialog =
                            if (isPreview || userModel.projectBadges.contains(args.projectId)) {
                                DialogType.LEAVE
                            } else {
                                DialogType.JOIN
                            }

                    }) {
                    if (isPreview || userModel.projectBadges.contains(args.projectId)) {
                        Text("Lecsatlakozás")
                    } else {
                        Text("Csatlakozás")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                when (showDialog) {
                    DialogType.MEMBERS -> {
                        ProjectMembersDialog(
                            members = members,
                            onMemberClick = { user ->
                                findNavController().navigate(
                                    DetailsFragmentDirections.actionGlobalMemberFragment(
                                        user
                                    )
                                )
                            },
                            onDismiss = {
                                showDialog = DialogType.NONE
                            }
                        )
                    }

                    DialogType.JOIN -> {
                        JoinAlertDialog(
                            onConfirm = {
                                joinProject(project)
                                refreshCurrentUserAndUserModel(requireContext())
                            },
                            onDismiss = {
                                showDialog = DialogType.NONE
                            }
                        )
                    }

                    DialogType.LEAVE -> {
                        LeaveAlertDIalog(
                            onConfirm = {
                                leaveProject()
                            },
                            onDismiss = {
                                showDialog = DialogType.NONE
                            }
                        )
                    }

                    DialogType.NONE -> {}
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = project.icon,
                        placeholder = painterResource(id = R.drawable.no_image_icon),
                        contentDescription = "Project icon",
                        modifier = Modifier
                            .size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            project.name,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                        ) {
                            BadgeIcon(project.maxBadges.toString())
                            ProjectMembers(members = members, onMembersClick = {
                                showDialog = DialogType.MEMBERS
                            })
                        }
                    }
                }
                AdminButtonRow(project)

                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(16.dp),

                    ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        DataBlock("Kategória", project.categoryEnum)
                        DataBlock(
                            "Készítő",
                            if (isPreview) {
                                "Teszt Jenő"
                            } else {
                                creator.name
                            }
                        )
                        DataBlock(
                            "Határidő",
                            DateFormat.getDateInstance().format(project.created)
                        )
                        Text(
                            text = "Leírás", style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                val comment by viewModel.mostRecentComment.observeAsState(initial = null)
                LastCommentCard(comment)
            }
        }
    }

    @Composable
    private fun AdminButtonRow(project: Project?) {
        if (userModel.roleAtLeast(Role.AREA_MANAGER)) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                AdminButton(
                    modifier = Modifier
                        .weight(1f),
                    imageVector = Icons.Default.People,
                    contentDescription = "Edit members' badges",
                    onClick = {
                        findNavController().navigate(
                            DetailsFragmentDirections.actionDetailsFragmentToAdminPanelFragment(
                                project!!
                            )
                        )
                    }
                )
                AdminButton(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit project",
                ) {
                    findNavController().navigate(
                        DetailsFragmentDirections.actionDetailsFragmentToEditProjectFragment(
                            project!!
                        )
                    )
                }
            }

        }
    }

    @Composable
    private fun AdminButton(
        modifier: Modifier, imageVector: ImageVector,
        contentDescription: String,
        onClick: () -> Unit
    ) {
        IconButton(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary),
            onClick = {
                onClick()
            }) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }

    @Composable
    private fun ProjectMembersDialog(
        members: List<User>,
        onDismiss: () -> Unit,
        onMemberClick: (User) -> Unit
    ) = AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Projekttagok") },
        text = {
            LazyColumn {
                items(members) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onMemberClick(member) }
                    ) {
                        Row {
                            AsyncImage(
                                // jelenleg nincs elcache-elve ez a kép sem, szóval újra letöltődik, amikor rányomunk a ProjetMembersre
                                model = member.photoURL,
                                placeholder = painterResource(id = R.drawable.no_image_icon),
                                contentDescription = "Member icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )

    @Composable
    private fun JoinAlertDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) =
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Csatlakozás")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Text("Biztos, hogy szeretnél csatlakozni a projekthez?")
            })


    @Composable
    private fun LeaveAlertDIalog(onConfirm: () -> Unit, onDismiss: () -> Unit) =
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Lecsatlakozás")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Text("Biztos, hogy le szeretnél csatlakozni a projektről? A projekten szerzett mancsaid ekkor elvesznek.")
            })


    @Composable
    private fun LastCommentCard(comment: Comment?) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    findNavController().navigate(
                        DetailsFragmentDirections.actionDetailsFragmentToCommentsFragment(
                            args.projectId
                        )
                    )
                },
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Legutóbbi hozzászólás",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (comment == null) {
                    Text(
                        text = "Nincs még hozzászólás",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = comment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }


    @Composable
    fun ProjectMembers(
        members: List<User>,
        onMembersClick: () -> Unit
    ) {
        val membersToShow = 5
        val displayMembers = members.take(membersToShow)
        val extraMembers = members.size - membersToShow

        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp)
                .clickable {
                    onMembersClick()
                },
            horizontalArrangement = Arrangement.spacedBy((-16).dp)
        ) {
            displayMembers.forEach { member ->
                AsyncImage(
                    // jelenleg nincs elcache-elve ez a kép, szóval újra letöltődik, amikor átjövünk ide a listázós fragmentből
                    model = member.photoURL,
                    placeholder = painterResource(id = R.drawable.no_image_icon),
                    contentDescription = "Member icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }

            if (extraMembers > 0) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$extraMembers",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    private fun joinProject(project: Project) {
        UserService.addUsersToProject(
            args.projectId,
            listOf(userModel.documentId),
            {
                Log.i(
                    TAG,
                    "Adding ${userModel.documentId} to project ${args.projectId}"
                )
                Toast.makeText(
                    context,
                    "Sikeresen csatlakoztál!",
                    Toast.LENGTH_SHORT
                ).show()
                refreshCurrentUserAndUserModel(requireContext())
                UserService.getMembersForProject(args.projectId)
            },
            {
                Toast.makeText(
                    context,
                    "A csatlakozás sikertelen, kérlek próbáld újra később",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        CloudMessagingService.sendNotificationToUsersById(
            "Csatlakoztak egy projekthez",
            "${userModel.name} csatlakozott a(z) \"${project.name}\" nevű projekthez!",
            listOf(project.creator + project.projectLeader)
        )
    }

    private fun leaveProject() {
        UserService.removeUserFromProject(
            args.projectId,
            userModel.documentId,
            {
                Log.i(
                    TAG,
                    "Removing ${userModel.documentId} from project ${args.projectId}"
                )
                Toast.makeText(context, "Sikeresen lecsatlakoztál!", Toast.LENGTH_SHORT)
                    .show()
                refreshCurrentUserAndUserModel(requireContext())
                UserService.getMembersForProject(args.projectId)
            },
            {
                Toast.makeText(
                    context,
                    "A lecsatlakozás sikertelen, kérlek próbáld újra később",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun setupTopMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(R.id.share, R.id.share, 0, R.string.share)
                    .setIcon(R.drawable.ic_share)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.share -> {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            //this should match the deeplink in the nav_graph. ikr it's ugly
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "mokapp-51f86.web.app/project/${args.projectId}"
                            )
                            putExtra(Intent.EXTRA_TITLE, viewModel.project.value!!.name)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}