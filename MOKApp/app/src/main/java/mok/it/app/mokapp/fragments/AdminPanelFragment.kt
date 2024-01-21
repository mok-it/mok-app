package mok.it.app.mokapp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_badge.view.mandatoryTextView
import kotlinx.android.synthetic.main.card_badge.view.projectDescription
import kotlinx.android.synthetic.main.card_badge.view.projectIcon
import kotlinx.android.synthetic.main.card_project_participant.badgeSlider
import kotlinx.android.synthetic.main.card_project_participant.maximumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.minimumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.view.badgeSlider
import kotlinx.android.synthetic.main.card_project_participant.view.maximumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.view.minimumBadgeValue
import kotlinx.android.synthetic.main.card_project_participant.view.participantName
import kotlinx.android.synthetic.main.card_project_participant.view.participantPicture
import kotlinx.android.synthetic.main.fragment_admin_panel.addParticipant
import kotlinx.android.synthetic.main.fragment_admin_panel.participants
import kotlinx.android.synthetic.main.fragment_all_badges_list.recyclerView
import kotlinx.android.synthetic.main.fragment_details.avatar_imagebutton
import kotlinx.android.synthetic.main.fragment_details.badgeComments
import kotlinx.android.synthetic.main.fragment_details.badgeCreator
import kotlinx.android.synthetic.main.fragment_details.badgeDeadline
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.service.UserService
import mok.it.app.mokapp.utility.Utility
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.log
import kotlin.math.roundToInt

class AdminPanelFragment : Fragment() {
    companion object {
        const val TAG = "AdminPanelFragment"
    }
    //TODO: using args.project.id to get the project because it should be updated. Could pass id only
    private val args: AdminPanelFragmentArgs by navArgs()
    private lateinit var project: Project
    private lateinit var userBadges: MutableMap<String, Int>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseUserObject.currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            initUserBadges()
        }
    }

    private fun initRecyclerView() {
        var adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        participants.adapter = adapter
        participants.layoutManager = WrapContentLinearLayoutManager(context)

    }


    private fun participantsQuery(): Query {
            return Firebase.firestore.collection(Collections.users)
                .orderBy("name", Query.Direction.ASCENDING)
                .whereIn(FieldPath.documentId(), project.members) //NOTE: can not handle lists of size greater than 30
    }
    private fun getAdapter(): FirestoreRecyclerAdapter<User, ProjectViewHolder> {
        val query = participantsQuery()
        val options =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<User, ProjectViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
                val view = LayoutInflater.from(this@AdminPanelFragment.context)
                    .inflate(R.layout.card_project_participant, parent, false)

                return ProjectViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                user: User
            ) {
                val tvName: TextView = holder.itemView.participantName
                val tvMaxBadge: TextView = holder.itemView.maximumBadgeValue
                val tvMinBadge: TextView = holder.itemView.minimumBadgeValue
                val ivImg: ImageView = holder.itemView.participantPicture
                val slBadge: RangeSlider = holder.itemView.badgeSlider
                tvName.text = user.name
                Picasso.get().load(user.photoURL).into(ivImg)
                tvMaxBadge.text = project.value.toString()
                slBadge.bottom = 0
                slBadge.top = project.value
                slBadge.stepSize = 1f
                slBadge.setValues(userBadges[user.documentId]?.toFloat() ?: 0f)
                tvMinBadge.text = "0"
                tvMaxBadge.text = project.value.toString()
                slBadge.setLabelFormatter { value -> value.toString() } //TODO: does not work currently
                slBadge.addOnChangeListener { _slider, value, _fromUser ->
                    UserService.addBadges(user.documentId, project.id, value.roundToInt(),
                        {
                            Log.i("AdinPanelFragment", "Badge count of user ${user.documentId} on project ${project.id} was set to $value")
                            userBadges[user.documentId] = value.roundToInt()
                    },
                        {
                            slBadge.setValues(userBadges[user.documentId]?.toFloat() ?: 0f)
                            Log.e(TAG, "Could not set badge count " +
                                    "on project ${project.id} for user ${user.documentId}")
                            Toast.makeText(context,
                                "Mancsok módosítása sikertelen." +
                                        " Kérlek ellenőrizd a kapcsolatot" +
                                        " vagy próbáld újra később.",
                                Toast.LENGTH_LONG)
                                .show()
                        })
                }
            }
        }
    }
    private fun initLayout() {
        addParticipant.setOnClickListener {
            findNavController().navigate(
                AdminPanelFragmentDirections
                    .actionAdminPanelFragmentToAddParticipantsDialogFragment(args.project.id)
            )
        }
    }

    private fun initUserBadges() {
        UserService.getProjectUsersAndBadges(
            args.project.id,
            {userBadges = it.toMutableMap()
                getProjectData()
            },
            {}
        )
    }

    private fun getProjectData() {
        Firebase.firestore.collection(Collections.badges).document(args.project.id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    project = document.toObject(Project::class.java)!!
                    initLayout()
                    initRecyclerView()
                }
            }
    }
}


