package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DateFormat.getDateInstance
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentDetailsBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.fragments.viewmodels.DetailsFragmentViewModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService
import mok.it.app.mokapp.utility.Utility.getIconFileName
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

class DetailsFragment : Fragment() {

    companion object {
        const val TAG = "DetailsFragment"
    }

    //TODO test whether it updates itself when the "members" tab is open
    // if not, use FirestoreRecyclerAdapter instead

    private val args: DetailsFragmentArgs by navArgs()

    //TODO refactor this to make proper use of the viewModel
    private val viewModel: DetailsFragmentViewModel by viewModels()

    private lateinit var project: Project

    lateinit var model: Project

    private var userIsEditor: Boolean = false
    private lateinit var _binding: FragmentDetailsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            setupTopMenu()
            refreshCurrentUserAndUserModel(requireContext()) {
                getMemberIds()
                getCommentIds()
                initLayout()
            }
            viewModel.members.observe(viewLifecycleOwner) {
                viewModel.members.value?.apply { initMembers() }
            }
        }
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
                            putExtra(Intent.EXTRA_TITLE, project.name)
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

    override fun onResume() {
        super.onResume()
        initLayout()
    }

    private fun initLayout() {
        binding.membersOverlayButton.setOnClickListener {
            if (viewModel.members.value?.isNotEmpty() == true && ::project.isInitialized) {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToProjectMembersDialogFragment(
                        viewModel.members.value!!,
                        userIsEditor,
                        project
                    )
                )
            }
        }
        binding.joinOrLeaveProjectButton.setOnClickListener {
            joinOrLeaveButtonPressed()
            refreshCurrentUserAndUserModel(requireContext())
        }
        binding.joinOrLeaveProjectButton.visibility = View.GONE
        binding.projectComments.setOnClickListener {
            val action =
                DetailsFragmentDirections.actionDetailsFragmentToCommentsFragment(args.projectId)
            findNavController().navigate(action)
        }
        Firebase.firestore.collection(Collections.PROJECTS).document(args.projectId).get()
            .addOnSuccessListener { document ->
                project = document.toObject(Project::class.java)!!
                binding.projectName.text = project.name
                binding.categoryName.text =
                    getString(R.string.specific_category, project.categoryEnum)
                binding.badgeValueTextView.text =
                    getString(R.string.specific_value, project.maxBadges)
                binding.projectCreateDescription.text = project.description

                Firebase.firestore.collection(Collections.USERS)
                    .document(project.creator)
                    .get().addOnSuccessListener { creatorDoc ->
                        if (creatorDoc?.get("name") != null) {
                            binding.projectCreator.text =
                                creatorDoc["name"] as String //TODO NPE itt is
                        }
                        val formatter = getDateInstance()
                        binding.projectDeadline.text =
                            formatter.format(project.created)
                        val iconFileName = getIconFileName(project.icon)
                        val iconFile = File(context?.filesDir, iconFileName)
                        if (iconFile.exists()) {
                            Log.i(TAG, "loading badge icon " + iconFile.path)
                            val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
                            binding.avatarImagebutton.setImageBitmap(bitmap)
                        } else {
                            Log.i(TAG, "downloading badge icon " + project.icon)
                            val callback = object : Callback {
                                override fun onSuccess() {
                                    // save image
                                    Log.i(TAG, "saving badge icon " + iconFile.path)
                                    val bitmap: Bitmap =
                                        binding.avatarImagebutton.drawable.toBitmap()
                                    val fos: FileOutputStream?
                                    try {
                                        fos = FileOutputStream(iconFile)
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                        fos.flush()
                                        fos.close()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onError(e: java.lang.Exception?) {
                                    Log.e(TAG, e.toString())
                                }
                            }
                            Picasso.get().load(project.icon)
                                .into(binding.avatarImagebutton, callback)
                        }

                        val editors = project.leaders
                        if (editors.contains(userModel.documentId)) {
                            userIsEditor = true
                        }
                        changeVisibilities()
                        initEditButton()
                        initAdminButton()
                    }
                changeVisibilities()
            }
    }

    private fun initEditButton() {
        if (userModel.isCreator || userModel.admin || project.creator == userModel.documentId || userIsEditor) {
            binding.editButton.visibility = View.VISIBLE
            binding.editButton.setOnClickListener {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToEditProjectFragment(
                        project
                    )
                )
            }
        }
    }

    private fun initAdminButton() {
        if (userModel.isCreator || userModel.admin || project.creator == userModel.documentId || userIsEditor) {
            binding.rewardButton.visibility = View.VISIBLE
            binding.rewardButton.setOnClickListener {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToAdminPanelFragment(
                        project
                    )
                )
            }
        }
    }


    /**
     * We adjust the extra member counter's text size based on the length of it
     */
    private fun initExtraMemberCounter(numOfExtraMembers: Int) {

        val textSizeResource = when ("$numOfExtraMembers".length) {
            1 -> R.dimen.profile_circle_leftover_text_size_1_digit
            2 -> R.dimen.profile_circle_leftover_text_size_2_digit
            3 -> R.dimen.profile_circle_leftover_text_size_3_digit
            else -> R.dimen.profile_circle_leftover_text_size_3_digit
        }

        val textSizeSP = resources.getDimension(textSizeResource)

        Log.d(TAG, "textSizeSP = $textSizeSP")

        binding.membersLeftNumber.text = getString(R.string.extra_members, numOfExtraMembers)
        binding.membersLeftNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSP)

    }

    private fun joinOrLeaveButtonPressed() {
        if (userModel.joinedBadges.contains(args.projectId)) { //dialog to leave project
            (context as Activity).let {
                MaterialDialog.Builder(it)
                    .setTitle(it.getString(R.string.leave_project))
                    .setMessage(it.getString(R.string.leave_project_message))
                    .setPositiveButton(it.getString(R.string.yes)) { dialogInterface, _ ->
                        leaveProject()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(it.getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                    .show()
            }
        } else { // dialog to join project
            (context as Activity).let {
                MaterialDialog.Builder(it)
                    .setTitle(it.getString(R.string.join_project))
                    .setMessage(it.getString(R.string.join_project_message, project.name))
                    .setPositiveButton(it.getString(R.string.yes)) { dialogInterface, _ ->
                        joinProject()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(it.getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                    .show()
            }
        }
    }

    private fun joinProject() {
        UserService.joinUsersToProject(
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
                getMemberIds()
                changeVisibilities()
            },
            {
                Toast.makeText(
                    context,
                    "A csatlakozás sikertelen, kérlek próbáld újra később.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        MyFirebaseMessagingService.sendNotificationToUsersById(
            "Csatlakoztak egy projekthez",
            "${userModel.name} csatlakozott a(z) \"${project.name}\" nevű mancshoz!",
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
                getMemberIds()
                changeVisibilities()
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

    private lateinit var memberComments: ArrayList<Comment>

    private fun getMemberIds() {
        val docRef = Firebase.firestore.collection(Collections.PROJECTS).document(args.projectId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    model = document.toObject(Project::class.java)!!
                    viewModel.getMembers(model.members)
                    Log.d(TAG, "Model data: $model")
                    changeVisibilities()
                    binding.joinOrLeaveProjectButton.isEnabled = true
                } else {
                    Log.d(TAG, "No such document or data is null")
                    changeVisibilities()
                    binding.joinOrLeaveProjectButton.isEnabled = true
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCommentIds() {
        memberComments = ArrayList()
        val collectionRef =
            Firebase.firestore.collection(Collections.PROJECTS).document(args.projectId)
                .collection(Collections.COMMENTS)
        collectionRef.get()
            .addOnSuccessListener { collection ->
                if (collection != null && collection.documents.isNotEmpty()) {
                    for (document in collection.documents) {
                        val comment = document.toObject(Comment::class.java)!!
                        memberComments.add(comment)
                    }
                    memberComments.sortByDescending { comment: Comment -> comment.time.toDate() }

                    val formatter = SimpleDateFormat("yyyy.MM.dd. hh:mm")
                    val timeString: String = formatter.format(memberComments[0].time.toDate())

                    // Search user with given uid among the members
                    var sender = "anonymous"
                    val docRef =
                        Firebase.firestore.collection(Collections.USERS)
                            .document(memberComments[0].uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val user = document.toObject(User::class.java)!!
                                sender = user.name
                            }
                            binding.projectComments.text = getString(
                                R.string.newest_comment_text,
                                timeString,
                                sender,
                                memberComments[0].text
                            )
                        }
                } else {
                    Log.d(TAG, "No such collection")
                }
            }
    }

    private fun initMembers() {
        binding.membersLeft.isVisible = false //TODO sometimes throws an NPE (members_left is null)
        binding.membersLeftNumber.isVisible = false

        val members = listOf(binding.member1, binding.member2, binding.member3)

        for (i in 0 until 3) {
            if (viewModel.members.value!!.size > i) {
                Picasso.get().load(viewModel.members.value!![i].photoURL).into(members[i])
                members[i].isVisible = true
            } else {
                members[i].isVisible = false
            }
        }

        if (viewModel.members.value!!.size >= 4) {
            initExtraMemberCounter(viewModel.members.value!!.size - 3)
            binding.membersLeft.isVisible = true
            binding.membersLeftNumber.isVisible = true
        }
    }

    private fun changeVisibilities() {
        binding.joinOrLeaveProjectButton.visibility = View.VISIBLE
        when {
            userModel.collectedBadges.contains(project.id) -> binding.joinOrLeaveProjectButton.visibility =
                View.GONE

            userModel.joinedBadges.contains(project.id) -> binding.joinOrLeaveProjectButton.text =
                getString(R.string.leave)

            else -> binding.joinOrLeaveProjectButton.text = getString(R.string.join)
        }

        if (project.leaders.contains(userModel.documentId)) {
            userIsEditor = true
        }
    }
}