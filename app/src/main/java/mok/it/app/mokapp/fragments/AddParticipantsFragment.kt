package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.CardSelectMemberBinding
import mok.it.app.mokapp.databinding.FragmentAddParticipantsBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.SelectMemberViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.service.UserService

class AddParticipantsFragment : DialogFragment() {
    companion object {
        val TAG = "AddParticipantsFragment"
    }

    private val args: AddParticipantsFragmentArgs by navArgs()
    private lateinit var project: Project
    private var selectedUsers: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentAddParticipantsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseUserObject.currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            Firebase.firestore.collection(Collections.PROJECTS).document(args.projectId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.data != null) {
                        project = document.toObject(Project::class.java)!!
                    }
                    FirebaseUserObject.refreshCurrentUserAndUserModel(requireContext()) {
                        initLayout()
                        initRecyclerView()
                    }
                }
        }
    }

    private fun initLayout() {
        binding.btnAddParticipants.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(
                    context,
                    "Előbb válassz résztvevőket a listából!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            binding.btnAddParticipants.isEnabled = false
            UserService.joinUsersToProject(project.id, selectedUsers, {
                Log.i(TAG, "Adding ${selectedUsers.size} users to project ${project.id}")
                Toast.makeText(context, "Résztvevők hozzáadva!", Toast.LENGTH_SHORT).show()
                MyFirebaseMessagingService.sendNotificationToUsersById(
                    "Új projekt",
                    "Hozzáadtak a(z) ${project.name} projekthez!",
                    selectedUsers
                )
                findNavController().popBackStack()

            }, {
                Toast.makeText(
                    context,
                    "A résztvevők hozzáadása sikertelen, kérlek próbáld újra később.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnAddParticipants.isEnabled = true
            }
            )
        }
    }

    private fun initRecyclerView() {
        var adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.nonParticipantsList.adapter = adapter
        binding.nonParticipantsList.layoutManager = WrapContentLinearLayoutManager(context)
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<User, SelectMemberViewHolder> {
        val query = usersQuery()
        val options =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<User, SelectMemberViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                SelectMemberViewHolder(
                    CardSelectMemberBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            override fun onBindViewHolder(
                holder: SelectMemberViewHolder,
                position: Int,
                user: User
            ) {
                val tvName: TextView = holder.binding.memberName
                val ivImg: ImageView = holder.binding.memberPicture
                val cbSelect: CheckBox = holder.binding.memberSelect
                tvName.text = user.name
                Picasso.get().load(user.photoURL).into(ivImg)
                cbSelect.setOnCheckedChangeListener(null)
                if (user.joinedBadges.contains(project.id)) {
                    cbSelect.isEnabled = false
                    cbSelect.isChecked = true
                } else {
                    cbSelect.isEnabled = true
                    cbSelect.isChecked = selectedUsers.contains(user.documentId)
                    cbSelect.setOnCheckedChangeListener { _, enabled ->
                        if (enabled) {
                            selectedUsers.add(user.documentId)
                        } else {
                            selectedUsers.remove(user.documentId)
                        }
                    }
                }
            }
        }
    }

    private fun usersQuery(): Query {
        return Firebase.firestore.collection(Collections.USERS)
            .orderBy("name", Query.Direction.ASCENDING)
    }
}