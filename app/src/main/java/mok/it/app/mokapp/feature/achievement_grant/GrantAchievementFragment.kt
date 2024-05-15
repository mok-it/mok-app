package mok.it.app.mokapp.feature.achievement_grant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.ui.model.UserAchievementLevelUi

class GrantAchievementFragment : Fragment() {
    private val args: GrantAchievementFragmentArgs by navArgs()
    private val viewModel: GrantAchievementViewModel by viewModels {
        GrantAchievementViewModelFactory(args.achievementId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val maxLevel by viewModel.maxLevel.collectAsState(1)
                val users by viewModel.users.collectAsState()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(users) { user ->
                            UserAmountCard(user, maxLevel) { amount ->
                                viewModel.onEvent(
                                    GrantAchievementEvent.SetAmount(amount, user)
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.onEvent(GrantAchievementEvent.Save)
                            findNavController().popBackStack()
                        },
                    ) {
                        Text(text = "Mentés")
                    }
                }
            }
        }
    }
}


@Composable
fun UserAmountCard(user: UserAchievementLevelUi, maxAmount: Int, onAmountChange: (Int) -> Unit) {

    Card(modifier = Modifier.padding(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.photoURL,
                contentDescription = "User photo",
                modifier = Modifier
                    .padding(6.dp)
                    .width(50.dp)
                    .height(50.dp)
                    .clip(CircleShape)
            )
            Text(text = user.name, modifier = Modifier.weight(1f))
            if (maxAmount == 1) {
                Checkbox(
                    checked = user.ownedLevel == 1,
                    onCheckedChange = { onAmountChange(if (it) 1 else 0) })
            } else {
                Button(
                    onClick = { onAmountChange(user.ownedLevel - 1) },
                    enabled = user.ownedLevel > 0,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "remove"
                    )
                }
                Text(
                    text = user.ownedLevel.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(12.dp)
                )
                Button(
                    onClick = { onAmountChange(user.ownedLevel + 1) },
                    enabled = user.ownedLevel < maxAmount,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "add"
                    )
                }
            }
        }

    }

}