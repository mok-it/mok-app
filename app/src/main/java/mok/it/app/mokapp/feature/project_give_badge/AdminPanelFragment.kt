package mok.it.app.mokapp.feature.project_give_badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.UserIcon
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
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
            MokAppTheme {
                AdminPanelScreen()
            }
        }
    }

    @Composable
    fun AdminPanelScreen() {
        val project by viewModel.project.observeAsState(initial = Project())
        val members by viewModel.members.observeAsState(initial = emptyList())

        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            bottomBar = {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    onClick = {
                        findNavController().navigate(
                            AdminPanelFragmentDirections
                                .actionAdminPanelFragmentToAddOrRemoveMembersDialogFragment(args.projectId)
                        )
                    },
                ) {
                    Text(text = "Projekttaglista módosítása")
                }
            }
        )
        { padding ->
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .padding(8.dp)
                            .height(IntrinsicSize.Min),
                    ) {
                        OutlinedIconButton(
                            modifier = Modifier
                                .height(50.dp)
                                .padding(horizontal = 4.dp)
                                .weight(0.5f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            enabled = uiState.stateModified,
                            onClick = {
                                viewModel.saveAllUserBadges()
                                Toast.makeText(
                                    requireContext(),
                                    "Módosítások sikeresen mentve!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save modifications",
                            )
                        }
                        OutlinedIconButton(
                            modifier = Modifier
                                .height(50.dp)
                                .padding(horizontal = 4.dp)
                                .weight(0.5f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            enabled = uiState.stateModified,
                            onClick = {
                                viewModel.resetSliderValues()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = "Reset modifications"
                            )
                        }
                    }
                    LazyColumn {
                        items(members) { member ->
                            MemberSliderCard(member, project, uiState)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MemberSliderCard(user: User, project: Project, uiState: AdminPanelUiState) {
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
                    value = uiState.sliderValues.getOrDefault(user.documentId, 0).toFloat(),
                    onValueChange = {
                        viewModel.updateSliderValue(user.documentId, it.roundToInt())
                    },
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
                    text = uiState.sliderValues.getOrDefault(user.documentId, 0).toString(),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
