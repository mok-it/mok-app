package mok.it.app.mokapp.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.viewmodels.ProfileViewModel
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService

@Composable
fun UserCard(
    user: User,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = user.photoURL,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                text = user.phoneNumber.ifEmpty { context.getString(R.string.no_phone_number) },
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProjectBadgeSummary(viewModel: ProfileViewModel) {
    val context = LocalContext.current
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
                text = context.getString(
                    R.string.collectedBadgesSummary,
                    projectDataListState.sumOf { it.finishedProjectBadgeSum }
                ),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = context.getString(
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
