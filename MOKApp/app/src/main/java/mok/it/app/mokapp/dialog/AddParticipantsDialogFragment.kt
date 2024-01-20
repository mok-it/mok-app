package mok.it.app.mokapp.dialog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.google.android.material.slider.RangeSlider
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_project_participant.view.badgeSlider
import kotlinx.android.synthetic.main.card_project_participant.view.maximumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.view.minimumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.view.participantName
import kotlinx.android.synthetic.main.card_project_participant.view.participantPicture
import kotlinx.android.synthetic.main.card_select_member.view.cbSelect
import kotlinx.android.synthetic.main.card_select_member.view.memberName
import kotlinx.android.synthetic.main.card_select_member.view.memberPicture
import kotlinx.android.synthetic.main.fragment_add_participants_dialog.btnAddParticipants
import kotlinx.android.synthetic.main.fragment_add_participants_dialog.nonParticipantsList
import kotlinx.android.synthetic.main.fragment_admin_panel.participants
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.fragments.AdminPanelFragment
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.service.UserService
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class AddParticipantsDialogFragment : DialogFragment() {
    companion object{
        val TAG = "AddParticipantsDialogFragment"
    }
    private val args: AddParticipantsDialogFragmentArgs by navArgs()
    private lateinit var project: Project

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_participants_dialog, container, false)
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
                }
            FirebaseUserObject.refreshCurrentUserAndUserModel(requireContext()) {
                initLayout()
                initRecyclerView()
            }
        }
    }

    private fun initLayout() {
        btnAddParticipants.setOnClickListener {
            Toast.makeText(context, "Hamarosan...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() { //TODO: terrible copy-paste boilerplate!
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
                val view = LayoutInflater.from(this@AddParticipantsDialogFragment.context)
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
                tvName.text = user.name
                Picasso.get().load(user.photoURL).into(ivImg)
            }
        }
    }

    private fun nonParticipantsQuery(): Query {
        return Firebase.firestore.collection(Collections.users)
            .orderBy("__name__", Query.Direction.ASCENDING)
            .whereNotIn(FieldPath.documentId(), project.members) //NOTE: can not handle lists of size greater than 30
    }
}