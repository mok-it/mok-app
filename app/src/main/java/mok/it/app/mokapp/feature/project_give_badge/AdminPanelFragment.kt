package mok.it.app.mokapp.feature.project_give_badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import kotlin.math.roundToInt

class AdminPanelFragment : Fragment() {
    private val args: AdminPanelFragmentArgs by navArgs()

    private val viewModel: AdminPanelViewModel by viewModels {
        AdminPanelViewModelFactory(args.projectId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AdminPanelScreen()
        }
    }

    @Composable
    fun AdminPanelScreen() {
        val members by viewModel.members.observeAsState(initial = emptyList())

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 150.dp)
            ) {
                items(members) { member ->
                    MemberSliderCard(member)
                }
            }
            Button(
                onClick = { /*TODO Handle add participant click */ },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Text(text = "Projekttag hozzáadása")
            }
        }
    }

    @Composable
    fun MemberSliderCard(user: User) {
        val project by viewModel.project.observeAsState(initial = Project())
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.photoURL,
                    contentDescription = "Participant Picture",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = user.name,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                Slider(
                    value = user.projectBadges[args.projectId]?.toFloat() ?: 0f,
                    onValueChange = { }, // we don't do anything on value change, only on save
                    valueRange = 0f..project.maxBadges.toFloat(),
                    steps = 1,
                    modifier = Modifier.weight(1f)
                )

                /*    binding.addParticipant.setOnClickListener {
            findNavController().navigate(
                AdminPanelFragmentDirections
                    .actionAdminPanelFragmentToAddParticipantsDialogFragment(args.projectId)
            )
        }
                * */
            }
        }
    }

    fun onSave(user: User, value: Float) {
        viewModel.addBadges(user, value.roundToInt()) {
            //on error, reset the slider's value to represent the actual value stored
//            slBadge.setValues(
//                viewModel.userBadges.value?.get(user.documentId)?.toFloat() ?: 0f
//            )
            Toast.makeText(
                context,
                "Mancsok módosítása sikertelen. Kérlek, ellenőrizd a kapcsolatot, " +
                        " vagy próbáld újra később.",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    fun completed(userId: String, project: Project) { //TODO this should be used somewhere
        viewModel.projectCompleted(userId, project)
    }
}
