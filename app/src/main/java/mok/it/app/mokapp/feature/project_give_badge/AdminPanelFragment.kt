package mok.it.app.mokapp.feature.project_give_badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.UserIcon
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
        val project by viewModel.project.observeAsState(initial = Project())
        val members by viewModel.members.observeAsState(initial = emptyList())
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        findNavController().navigate(
                            AdminPanelFragmentDirections
                                .actionAdminPanelFragmentToAddParticipantsDialogFragment(args.projectId)
                        )
                    },
                ) {
                    Text(text = "Projekttag hozzáadása")
                }
                Button(
                    modifier = Modifier
                        .weight(0.5f),
                    enabled = uiState.stateModified,
                    onClick = {
//                    saveEveryUser()
                    },
                ) {
                    Text(text = "Mentés")
                }
                OutlinedButton(
                    modifier = Modifier
                        .weight(0.5f),
                    enabled = uiState.stateModified,
                    onClick = {
//                    resetEveryUser()
                    },
                ) {
                    Text(text = "Visszaállítás")
                }
            }
            LazyColumn {
                items(members) { member ->
                    MemberSliderCard(member, project)
                }
            }
        }
    }

    @Composable
    fun MemberSliderCard(user: User, project: Project) {
        var sliderValue by remember {
            mutableFloatStateOf(
                user.projectBadges[args.projectId]?.toFloat() ?: 0f
            ) //ez most így elveszik configuration change-eknél, vm-be kéne
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserIcon(user = user, navController = findNavController())

                Text(
                    text = user.name,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..project.maxBadges.toFloat(),
                    steps = project.maxBadges,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.inversePrimary,
                    ),
                )

                Text(
                    text = sliderValue.toInt().toString(),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }

    fun saveEveryUser(user: User, value: Float) {
        //TODO: save every user's badge count
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
