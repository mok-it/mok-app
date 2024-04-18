package mok.it.app.mokapp.fragments

//import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
//import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.compose.DataBlock
import mok.it.app.mokapp.compose.parameterproviders.ProjectParamProvider
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.CloudMessagingService
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.fragments.viewmodels.DetailsViewModel
import mok.it.app.mokapp.fragments.viewmodels.DetailsViewModelFactory
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG
import java.text.DateFormat

class DetailsFragment : Fragment() {

    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModelFactory(args.projectId)
    }

    enum class DialogType {
        NONE, JOIN, LEAVE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupTopMenu()
        return ComposeView(requireContext()).apply {
            setContent {
                val project by viewModel.project.observeAsState(initial = Project())
                DetailsScreen(project)
            }
        }
    }

    // ha a FirebaseUserObject refaktorálva lesz, ez a függvény is sokat egyszerűsödik majd
    @Preview
    @Composable
    private fun DetailsScreen(
        @PreviewParameter(ProjectParamProvider::class) project: Project
    ) {
        var showDialog by remember { mutableStateOf(DialogType.NONE) }
        val isPreview = LocalInspectionMode.current

        Scaffold(
            bottomBar = {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {

                        showDialog =
                            if (isPreview || userModel.joinedBadges.contains(args.projectId)) {
                                DialogType.LEAVE
                            } else {
                                DialogType.JOIN
                            }

                    }) {
                    if (isPreview || userModel.joinedBadges.contains(args.projectId)) {
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
                    DialogType.JOIN -> {
                        joinProject(project)
                        showDialog = DialogType.NONE
                    }

                    DialogType.LEAVE -> {
                        leaveProject()
                        showDialog = DialogType.NONE
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
                            BadgeIcon(project.maxBadges)
                            ProjectMembers(project)
                        }
                    }
                }

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
                                viewModel.creatorUser.value?.name ?: "Ismeretlen készítő"
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
                LastCommentCard(viewModel.mostRecentComment.value)
            }
        }

//        viewModel.project.observe(viewLifecycleOwner) { project ->
//            binding.projectName.text = project.name
//            binding.categoryName.text =
//                getString(R.string.specific_category, project.categoryEnum)
//            binding.badgeValueTextView.text =
//                getString(R.string.specific_value, project.maxBadges)
//            binding.projectCreateDescription.text = project.description
//
//            binding.projectCreator.text = viewModel.creatorUser.value?.name
//            val formatter = DateFormat.getDateInstance()
//            binding.projectDeadline.text =
//                formatter.format(project.created)
//            val iconFileName = Utility.getIconFileName(project.icon)
//            val iconFile = File(context?.filesDir, iconFileName)
//            if (iconFile.exists()) {
//                Log.i(TAG, "loading badge icon " + iconFile.path)
//                val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
//                binding.avatarImagebutton.setImageBitmap(bitmap)
//            } else {
//                Log.i(TAG, "downloading badge icon " + project.icon)
//                val callback = object : Callback {
//                    override fun onSuccess() {
//                        // save image
//                        Log.i(TAG, "saving badge icon " + iconFile.path)
//                        val bitmap: Bitmap =
//                            binding.avatarImagebutton.drawable.toBitmap()
//                        val fos: FileOutputStream?
//                        try {
//                            fos = FileOutputStream(iconFile)
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
//                            fos.flush()
//                            fos.close()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onError(e: java.lang.Exception?) {
//                        Log.e(TAG, e.toString())
//                    }
//                }
//                Picasso.get().load(project.icon)
//                    .into(binding.avatarImagebutton, callback)
//            }
//
//            val editors = project.leaders
//            if (editors.contains(userModel.documentId)) {
//                userIsEditor = true
//            }

//        if (currentUser == null) {
//            findNavController().navigate(R.id.action_global_loginFragment)
//        } else {
//            setupTopMenu()
//            refreshCurrentUserAndUserModel(requireContext()) {
//                UserService.getMembersForProject(args.projectId)
//                initLayout()
//            }
//        }
//
//        viewModel.members.observe(viewLifecycleOwner) {
//            initMembers()
//        }
//
//        if (userModel.isCreator || userModel.admin || viewModel.project.value!!.creator == userModel.documentId || userIsEditor) {
//            binding.rewardButton.visibility = View.VISIBLE
//            binding.rewardButton.setOnClickListener {
//                findNavController().navigate(
//                    DetailsFragmentDirections.actionDetailsFragmentToAdminPanelFragment(
//                        viewModel.project.value!!
//                    )
//                )
//            }
//        }

//        binding.joinOrLeaveProjectButton.visibility = View.VISIBLE
//        when {
//            userModel.collectedBadges.contains(viewModel.project.value!!.id) -> binding.joinOrLeaveProjectButton.visibility =
//                View.GONE
//
//            userModel.joinedBadges.contains(viewModel.project.value!!.id) -> binding.joinOrLeaveProjectButton.text =
//                getString(R.string.leave)
//
//            else -> binding.joinOrLeaveProjectButton.text = getString(R.string.join)
//        }
//
//        if (viewModel.project.value!!.leaders.contains(userModel.documentId)) {
//            userIsEditor = true
//        }


//        if (userModel.isCreator || userModel.admin || viewModel.project.value!!.creator == userModel.documentId || userIsEditor) {
//            binding.editButton.visibility = View.VISIBLE
//            binding.editButton.setOnClickListener {
//                findNavController().navigate(
//                    DetailsFragmentDirections.actionDetailsFragmentToEditProjectFragment(
//                        viewModel.project.value!!
//                    )
//                )
//            }
//        }

//        binding.membersOverlayButton.setOnClickListener {
//            if (viewModel.members.value?.isNotEmpty() == true && viewModel.project.value != null) {
//                findNavController().navigate(
//                    DetailsFragmentDirections.actionDetailsFragmentToProjectMembersDialogFragment(
//                        viewModel.members.value!!.toTypedArray(),
//                        userIsEditor,
//                        viewModel.project.value!!
//                    )
//                )
//            }
//        }
//        binding.joinOrLeaveProjectButton.setOnClickListener {
//            joinOrLeaveButtonPressed()
//            refreshCurrentUserAndUserModel(requireContext())
//        }
//        binding.joinOrLeaveProjectButton.visibility = View.GONE
//        binding.mostRecentComment.setOnClickListener {
//            val action =
//                DetailsFragmentDirections.actionDetailsFragmentToCommentsFragment(args.projectId)
//            findNavController().navigate(action)
//        }
//

    }

    @Composable
    private fun LastCommentCard(comment: Comment?) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
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


    @Preview
    @Composable
    fun ProjectMembers(
        @PreviewParameter(ProjectParamProvider::class) project: Project
    ) {
        val members =
            if (LocalInspectionMode.current) {
                listOf(
                    User(name = "Teszt Jenő"),
                    User(name = "Teszt József"),
                    User(name = "Teszt Béla"),
                    User(name = "Teszt Jenő"),
                    User(name = "Teszt József"),
                    User(name = "Teszt Béla")
                )
            } else {
                viewModel.members.observeAsState(initial = emptyList()).value
            }
        val displayMembers = members.take(5)
        val extraMembers = members.size - 5

        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy((-16).dp)
        ) {
            displayMembers.forEach { member ->
                AsyncImage(
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
                    "A csatlakozás sikertelen, kérlek próbáld újra később.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        CloudMessagingService.sendNotificationToUsersById(
            "Csatlakoztak egy projekthez",
            "${userModel.name} csatlakozott a(z) \"${project.name}\" nevű projekthez!",
            listOf(project.creator + project.leaders)
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
                    "A lecsatlakozás sikertelen, kérlek próbáld újra később.",
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
                                "mokegyesulet.hu/app/badges/${args.projectId}"
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