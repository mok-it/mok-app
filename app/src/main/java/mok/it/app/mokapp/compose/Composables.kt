package mok.it.app.mokapp.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.parameterproviders.RewardParamProvider
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.fragments.viewmodels.ProfileViewModel
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.model.User

@Composable
fun UserCard(
    user: User,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = user.photoURL,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.nickname.ifEmpty { "Nincs becenév megadva" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.phoneNumber.ifEmpty { stringResource(R.string.no_phone_number) },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ProjectBadgeSummary(viewModel: ProfileViewModel) {
    val projectDataListState =
        viewModel.userBadgeDataInEachCategory.observeAsState().value?.sortedByDescending { it.finishedProjectBadgeSum }
            ?: emptyList()

    Column {
        HorizontalDivider()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.collectedBadgesSummary,
                    projectDataListState.sumOf { it.finishedProjectBadgeSum }
                ),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.collectedProjectsSummary,
                    projectDataListState.sumOf { it.finishedProjectBadgeSum }
                ),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(projectDataListState) { badgeData ->
                BadgeCard(badgeData)
            }
        }
    }
}

@Composable
fun BadgeIcon(badgeNumber: String, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.badgeicon),
            contentDescription = "Badge icon",
            colorFilter = if(isEnabled) null else ColorFilter.tint(Color.Gray),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = badgeNumber,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp),
            color = if(isEnabled) Color.White else Color.DarkGray
        )
    }
}

@Preview
@Composable
fun RewardItem(
    @PreviewParameter(RewardParamProvider::class) reward: Reward,
    canBeBought: (Reward) -> Boolean = { true },
    onClick: (Reward) -> Unit = {}
) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        enabled = canBeBought(reward),
    )
    {
        Row(
            modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            AsyncImage(
                model = reward.icon,
                contentDescription = "Icon of the reward",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.no_image_icon),
                //TODO add placeholder loading animation
            )
            Column(modifier = Modifier.padding(4.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.quantity, reward.quantity),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(modifier = Modifier.padding(4.dp).requiredWidth(100.dp)) {
                BadgeIcon(reward.price.toString(), isEnabled = canBeBought(reward))
                if(canBeBought(reward))
                    IconButton(
                        onClick = { onClick(reward) },
                    )
                    {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Request reward",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
            }
        }
    }
}

@Composable
private fun BadgeCard(badgeData: UserService.BadgeData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = badgeData.category.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                badgeData.finishedProjectBadgeSum.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
