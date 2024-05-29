package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.R
import mok.it.app.mokapp.feature.profile_view.ProfileViewModel

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
