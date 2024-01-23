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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_select_member.view.memberName
import kotlinx.android.synthetic.main.card_select_member.view.memberPicture
import kotlinx.android.synthetic.main.card_select_member.view.memberSelect
import kotlinx.android.synthetic.main.fragment_add_participants.btnAddParticipants
import kotlinx.android.synthetic.main.fragment_add_participants.nonParticipantsList
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.service.UserService

class AddParticipantsFragment : DialogFragment() {
    companion object{
        val TAG = "AddParticipantsDialogFragment"
    }
    private val args: AddParticipantsFragmentArgs by navArgs()
    private lateinit var project: Project
    private var selectedUsers: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_participants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseUserObject.currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            Firebase.firestore.collection(Collections.badges).document(args.projectId).get()
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
        btnAddParticipants.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(context, "Előbb válassz résztvevőket a listából!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserService.joinUsersToProject( project.id, selectedUsers, {
                Log.i(TAG, "Adding ${selectedUsers.size} users to project ${project.id}")
                Toast.makeText(context, "Résztvevők hozzáadva!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()

                }, {
                    Toast.makeText(context, "A résztvevők hozzáadása sikertelen, kérlek próbáld újra később.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun initRecyclerView() {
        var adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        nonParticipantsList.adapter = adapter
        nonParticipantsList.layoutManager = WrapContentLinearLayoutManager(context)
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<User, ProjectViewHolder> {
        val query = nonParticipantsQuery()
        val options =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<User, ProjectViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@AddParticipantsFragment.context)
                    .inflate(R.layout.card_select_member, parent, false)

                return ProjectViewHolder(view)
            }

            //TODO: checkbox is on viewholder, should be on user instead. Need viewModel?
            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                user: User
            ) {
                val tvName: TextView = holder.itemView.memberName
                val ivImg: ImageView = holder.itemView.memberPicture
                val cbSelect: CheckBox = holder.itemView.memberSelect
                tvName.text = user.name
                Picasso.get().load(user.photoURL).into(ivImg)
                cbSelect.setOnCheckedChangeListener(null)
                cbSelect.isChecked = selectedUsers.contains(user.documentId)
                cbSelect.setOnCheckedChangeListener { _, enabled ->
                    if (enabled) {
                        selectedUsers.add(user.documentId)
                    }
                    else {
                        selectedUsers.remove(user.documentId)
                    }
                }
            }
        }
    }

    private fun nonParticipantsQuery(): Query {
        return Firebase.firestore.collection(Collections.users)
            .orderBy("__name__", Query.Direction.ASCENDING)
            .whereNotIn(FieldPath.documentId(), project.members) //NOTE: can not handle lists of size greater than 30
    }
}