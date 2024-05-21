package mok.it.app.mokapp.feature.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.ui.compose.achievements.AchievementCard
import mok.it.app.mokapp.ui.compose.projects.ProjectCard
import mok.it.app.mokapp.ui.compose.theme.ExtendedTheme
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.ui.model.AchievementUi

class TasksFragment : Fragment() {
    private val viewModel: TasksViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val achievements by viewModel.achievements.collectAsState(initial = emptyList())
                val projects by viewModel.projects.collectAsState(initial = emptyList())
                val earnedBadges by viewModel.earnedBadges.collectAsState(initial = 0)
                val requiredBadges = viewModel.requiredBadges
                MokAppTheme {
                    TasksScreen(
                        achievements,
                        { achievement ->
                            findNavController().navigate(
                                TasksFragmentDirections.actionTasksFragmentToAchievementDetailsFragment(
                                    achievement.id
                                )
                            )
                        },
                        projects,
                        { project ->
                            findNavController().navigate(
                                TasksFragmentDirections.actionTasksFragmentToDetailsFragment(
                                    project.id
                                )
                            )
                        },
                        earnedBadges,
                        requiredBadges
                    )
                }
            }
        }
}

@Composable
fun TasksScreen(
    achievements: List<AchievementUi>,
    onAchievementClck: (AchievementUi) -> Unit,
    projects: List<Project>,
    onProjectClick: (Project) -> Unit,
    earnedBadges: Int,
    requiredBadges: Int
) {
    Surface {
        LazyColumn {
            item { AllBadgesCard(earnedBadges, requiredBadges) }
            item {
                Text(
                    text = "Kötelező acsik:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                )
            }
            items(achievements) { achievement ->
                AchievementCard(achievement, true, onAchievementClck)
            }
            item {
                Text(
                    text = "Projektjeim:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                )
            }
            items(projects) { project ->
                ProjectCard(project, onProjectClick)
            }

        }
    }
}

@Composable
fun AllBadgesCard(earnedBadges: Int, requiredBadges: Int) {
    val color =
        if (earnedBadges >= requiredBadges) ExtendedTheme.colorScheme.success.color
        else ExtendedTheme.colorScheme.warning.colorContainer
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(color = color)) {
            append(
                earnedBadges.toString()
            )
        }
        append("/$requiredBadges")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Összes mancs",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.paw_solid),
                tint = ExtendedTheme.colorScheme.success.colorContainer,
                contentDescription = "Mancs",
                modifier = Modifier.size(50.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.paw_solid),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Mancs",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(50.dp)
            )
        }
    }
}