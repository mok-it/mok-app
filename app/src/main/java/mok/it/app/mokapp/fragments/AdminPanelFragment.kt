package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.CardProjectParticipantBinding
import mok.it.app.mokapp.databinding.FragmentAdminPanelBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.fragments.viewmodels.AdminPanelViewModel
import mok.it.app.mokapp.fragments.viewmodels.AdminPanelViewModelFactory
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.ProjectParticipantViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.loadImage
import kotlin.math.roundToInt

class AdminPanelFragment : Fragment() {
    //TODO: using args.project.id to get the project because it should be updated. Could pass id only
    private val args: AdminPanelFragmentArgs by navArgs()
    private lateinit var binding: FragmentAdminPanelBinding

    private val viewModel: AdminPanelViewModel by viewModels {
        AdminPanelViewModelFactory(args.project.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseUserObject.currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            viewModel.userBadges.observe(viewLifecycleOwner) {
                initLayout()
                initRecyclerView()
            }
        }
    }

    private fun initRecyclerView() {
        val adapter = getAdapter()
        if (adapter == null) {
            binding.adminParticipantsEmpty.root.visibility = View.VISIBLE
            binding.participant.visibility = View.GONE
            binding.badgeReward.visibility = View.GONE
            binding.participants.visibility = View.GONE
            return
        }
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.participants.adapter = adapter
        binding.participants.layoutManager = WrapContentLinearLayoutManager(context)

    }


    private fun participantsQuery(): Query {
        return Firebase.firestore.collection(Collections.users)
            .orderBy("name", Query.Direction.ASCENDING)
            .whereIn(
                FieldPath.documentId(),
                viewModel.project.value!!.members
            ) //NOTE: can not handle lists of size greater than 30
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<User, ProjectParticipantViewHolder>? {
        if (viewModel.project.value?.members?.isEmpty() != false) {
            return null
        }
        val query = participantsQuery()
        val options =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<User, ProjectParticipantViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ProjectParticipantViewHolder(
                    CardProjectParticipantBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            override fun onBindViewHolder(
                holder: ProjectParticipantViewHolder,
                position: Int,
                user: User
            ) {
                val tvName: TextView = holder.binding.participantName
                val tvMaxBadge: TextView = holder.binding.maximumBadgeValue
                val tvMinBadge: TextView = holder.binding.minimumBadgeValue
                val ivImg: ImageView = holder.binding.participantPicture
                val slBadge: RangeSlider = holder.binding.badgeSlider
                tvName.text = user.name
                loadImage(ivImg, user.photoURL, requireContext())
                tvMaxBadge.text = viewModel.project.value!!.maxBadges.toString()
                slBadge.valueFrom = 0f
                slBadge.valueTo = viewModel.project.value!!.maxBadges.toFloat()
                slBadge.bottom = 0
                slBadge.top = viewModel.project.value!!.maxBadges
                slBadge.stepSize = 1f
                slBadge.setValues(viewModel.userBadges.value?.get(user.documentId)?.toFloat() ?: 0f)
                tvMinBadge.text = "0"
                tvMaxBadge.text = viewModel.project.value!!.maxBadges.toString()
                slBadge.setLabelFormatter { value -> value.toString() }
                slBadge.addOnChangeListener { _, value, _ ->
                    viewModel.addBadges(user, value.roundToInt()) {
                        //on error, reset the slider's value to represent the actual value stored
                        slBadge.setValues(
                            viewModel.userBadges.value?.get(user.documentId)?.toFloat() ?: 0f
                        )
                        Toast.makeText(
                            context,
                            "Mancsok módosítása sikertelen. Kérlek, ellenőrizd a kapcsolatot, " +
                                    " vagy próbáld újra később.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun initLayout() {
        binding.addParticipant.setOnClickListener {
            findNavController().navigate(
                AdminPanelFragmentDirections
                    .actionAdminPanelFragmentToAddParticipantsDialogFragment(args.project.id)
            )
        }
    }

    fun completed(userId: String, project: Project) { //TODO this should be used somewhere
        viewModel.projectCompleted(userId, project)
    }
}
