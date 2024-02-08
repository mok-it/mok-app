package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
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

    private lateinit var badgeModel: Project

    lateinit var model: Project

    private var userIsEditor: Boolean = false
    private lateinit var _binding: FragmentDetailsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                                "mokegyesulet.hu/app/badges/${args.badgeId}"
                            )
                            putExtra(Intent.EXTRA_TITLE, badgeModel.name)
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
            if (viewModel.members.value?.isNotEmpty() == true && ::badgeModel.isInitialized) {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToBadgeMembersDialogFragment(
                        viewModel.members.value!!,
                        userIsEditor,
                        badgeModel
                    )
                )
            }
        }
        binding.joinButton.setOnClickListener {
            join()
            refreshCurrentUserAndUserModel(requireContext())
        }
        binding.joinButton.visibility = View.GONE
        binding.badgeComments.setOnClickListener {
            val action =
                DetailsFragmentDirections.actionDetailsFragmentToCommentsFragment(args.badgeId)
            findNavController().navigate(action)
        }
        Firebase.firestore.collection(Collections.badges).document(args.badgeId).get()
            .addOnSuccessListener { document ->
                badgeModel = document.toObject(Project::class.java)!!
                binding.badgeName.text = badgeModel.name
                binding.categoryName.text =
                    getString(R.string.specific_category, badgeModel.categoryEnum)
                binding.badgeValueTextView.text = getString(R.string.specific_value, badgeModel.value)
                binding.badgeDescription.text = badgeModel.description

                Firebase.firestore.collection(Collections.users)
                    .document(badgeModel.creator)
                    .get().addOnSuccessListener { creatorDoc ->
                        if (creatorDoc?.get("name") != null) {
                            binding.badgeCreator.text = creatorDoc["name"] as String //TODO NPE itt is
                        }
                        val formatter = getDateInstance()
                        binding.badgeDeadline.text =
                            formatter.format(badgeModel.created)

                        val iconFileName = getIconFileName(badgeModel.icon)
                        val iconFile = File(context?.filesDir, iconFileName)
                        if (iconFile.exists()) {
                            Log.i(TAG, "loading badge icon " + iconFile.path)
                            val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
                            binding.avatarImagebutton.setImageBitmap(bitmap)
                        } else {
                            Log.i(TAG, "downloading badge icon " + badgeModel.icon)
                            val callback = object : Callback {
                                override fun onSuccess() {
                                    // save image
                                    Log.i(TAG, "saving badge icon " + iconFile.path)
                                    val bitmap: Bitmap = binding.avatarImagebutton.drawable.toBitmap()
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
                            Picasso.get().load(badgeModel.icon).into(binding.avatarImagebutton, callback)
                        }

                        val editors = badgeModel.editors
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
        if (badgeModel.creator == userModel.documentId || userIsEditor) {
            binding.editButton.visibility = View.VISIBLE
            binding.editButton.setOnClickListener {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToEditBadgeFragment(
                        badgeModel
                    )
                )
            }
        }
    }

    private fun initAdminButton() {
        if (badgeModel.creator == userModel.documentId || userIsEditor) {
            binding.rewardButton.visibility = View.VISIBLE
            binding.rewardButton.setOnClickListener {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToAdminPanelFragment(
                        badgeModel
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

    private fun join() {
        if (userModel.joinedBadges.contains(args.badgeId)) {
            val userRef =
                Firebase.firestore.collection(Collections.users).document(currentUser!!.uid)
            userRef.update("joinedBadges", FieldValue.arrayRemove(args.badgeId))

            val badgeRef =
                Firebase.firestore.collection(Collections.badges).document(args.badgeId)
            badgeRef.update("members", FieldValue.arrayRemove(currentUser?.uid))
                .addOnCompleteListener {
                    Toast.makeText(context, "Sikeresen lecsatlakoztál!", Toast.LENGTH_SHORT).show()
                    getMemberIds()
                    changeVisibilities()
                }
        } else {
            val userRef =
                Firebase.firestore.collection(Collections.users).document(currentUser!!.uid)
            userRef.update("joinedBadges", FieldValue.arrayUnion(args.badgeId))

            val badgeRef =
                Firebase.firestore.collection(Collections.badges).document(args.badgeId)
            badgeRef.update("members", FieldValue.arrayUnion(currentUser?.uid))
                .addOnCompleteListener {
                    Toast.makeText(context, "Sikeresen csatlakoztál!", Toast.LENGTH_SHORT).show()
                    getMemberIds()
                    changeVisibilities()
                }
        }

        MyFirebaseMessagingService.sendNotificationToUsersById(
            "Csatlakoztak egy mancshoz",
            "${userModel.name} csatlakozott a(z) \"${badgeModel.name}\" nevű mancshoz!",
            listOf(badgeModel.creator + badgeModel.editors)
        )
    }

    private lateinit var memberComments: ArrayList<Comment>

    private fun getMemberIds() {
        val docRef = Firebase.firestore.collection(Collections.badges).document(args.badgeId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    model = document.toObject(Project::class.java)!!
                    viewModel.getMembers(model.members)
                    Log.d(TAG, "Model data: $model")
                } else {
                    Log.d(TAG, "No such document or data is null")
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCommentIds() {
        memberComments = ArrayList()
        val collectionRef =
            Firebase.firestore.collection(Collections.badges).document(args.badgeId)
                .collection(Collections.commentsRelativePath)
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
                        Firebase.firestore.collection(Collections.users)
                            .document(memberComments[0].uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val user = document.toObject(User::class.java)!!
                                sender = user.name
                            }
                            binding.badgeComments.text = getString(
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
        binding.joinButton.visibility = View.VISIBLE
        when {
            userModel.collectedBadges.contains(badgeModel.id) -> binding.joinButton.visibility = View.GONE
            userModel.joinedBadges.contains(badgeModel.id) -> binding.joinButton.text =
                getString(R.string.leave)

            else -> binding.joinButton.text = getString(R.string.join)
        }

        if (badgeModel.editors.contains(userModel.documentId)) {
            userIsEditor = true
        }
    }
}