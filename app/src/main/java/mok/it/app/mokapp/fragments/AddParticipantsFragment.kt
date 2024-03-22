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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import mok.it.app.mokapp.databinding.CardSelectMemberBinding
import mok.it.app.mokapp.databinding.FragmentAddParticipantsBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.firebase.service.UserService.getUsersQuery
import mok.it.app.mokapp.fragments.viewmodels.AddParticipantsViewModel
import mok.it.app.mokapp.fragments.viewmodels.AddParticipantsViewModelFactory
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.SelectMemberViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.TAG
import mok.it.app.mokapp.utility.Utility.loadImage

class AddParticipantsFragment : DialogFragment() {

    private val args: AddParticipantsFragmentArgs by navArgs()
    private var selectedUsers: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentAddParticipantsBinding

    private val viewModel: AddParticipantsViewModel by viewModels {
        AddParticipantsViewModelFactory(args.projectId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.project.observe(viewLifecycleOwner) {
            FirebaseUserObject.refreshCurrentUserAndUserModel(requireContext()) {
                initLayout()
                initRecyclerView()
            }
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleUserJoiningError(exception: Exception) {
        Log.e(TAG, "Error adding users to project ${viewModel.project.value!!.id}", exception)
        showToastMessage("A résztvevők hozzáadása sikertelen, kérlek, próbáld újra később.")
        binding.btnAddParticipants.isEnabled = true
    }

    private fun handleUserJoiningSuccess() {
        Log.i(TAG, "Adding ${selectedUsers.size} users to project ${viewModel.project.value!!.id}")
        showToastMessage("Résztvevők hozzáadva!")
        findNavController().popBackStack()
    }

    private fun initLayout() {
        binding.btnAddParticipants.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                showToastMessage("Előbb válassz résztvevőket a listából!")
                return@setOnClickListener
            }
            binding.btnAddParticipants.isEnabled = false
            UserService.addUsersToProject(
                viewModel.project.value!!.id,
                selectedUsers,
                ::handleUserJoiningSuccess,
                ::handleUserJoiningError
            )
        }
    }

    private fun setViewHolderData(holder: SelectMemberViewHolder, user: User) {
        val tvName: TextView = holder.binding.memberName
        val ivImg: ImageView = holder.binding.memberPicture
        val cbSelect: CheckBox = holder.binding.memberSelect
        tvName.text = user.name
        loadImage(ivImg, user.photoURL, requireContext())
        cbSelect.setOnCheckedChangeListener(null)
        if (user.joinedBadges.contains(viewModel.project.value?.id)) {
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

    private fun getAdapter(): FirestoreRecyclerAdapter<User, SelectMemberViewHolder> {
        val query = getUsersQuery()
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
                setViewHolderData(holder, user)
            }
        }
    }

    private fun initRecyclerView() {
        val adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.nonParticipantsList.adapter = adapter
        binding.nonParticipantsList.layoutManager = WrapContentLinearLayoutManager(context)
    }
}
