package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import dev.esteki.expandable.Expandable
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.viewmodels.MyBadgesViewModel
import mok.it.app.mokapp.model.Project

class MyBadgesFragment : Fragment() {

    private val viewModel: MyBadgesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            MyBadgesScreen() //TODO apply mokapp theme in the whole fragment
        }
    }

    @Composable
    private fun MyBadgesScreen() {
        val myCategories = viewModel.myCategories.observeAsState(initial = emptyList()).value

        // the column itself is not scrollable, only the opened Expandable is - this could be fixed
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            for (category in myCategories) {
                val expanded = rememberSaveable { mutableStateOf(false) }

                Card(modifier = Modifier.padding(8.dp)) {
                    Expandable(
                        modifier = Modifier.padding(8.dp),
                        expanded = expanded.value,
                        onExpandChanged = {
                            expanded.value = it
                        },
                        title = {
                            Text(
                                text = "$category (${viewModel.projectsInCategory(category).size})",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        content = {
                            LazyColumn {
                                items(viewModel.projectsInCategory(category)) { project ->
                                    MyProjectCard(
                                        project, userModel.projectBadges[project.id] ?: 0
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun MyProjectCard(project: Project, collectedBadges: Int) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable {
                    findNavController().navigate(
                        MyBadgesFragmentDirections.actionMyBadgesFragmentToDetailsFragment(
                            project.id
                        )
                    )
                }, shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                modifier = Modifier.padding(6.dp)
            ) {
                AsyncImage(
                    model = project.icon,
                    contentDescription = "Project Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = project.name,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    BadgeIcon(
                        badgeNumberText = collectedBadges.toString(),
                        isGreen = project.maxBadges <= collectedBadges
                    )
                }
            }
        }
    }
}