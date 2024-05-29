package mok.it.app.mokapp.ui.compose.projects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.ui.compose.BadgeIcon

@Composable
fun ImportProjectCard(project: Project) {

    Card(
            modifier = Modifier
		            .fillMaxWidth()
		            .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
    ) {
        Column {

            Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                        model = project.icon,
                        contentDescription = "Projekt ikon",
                        modifier = Modifier
		                        .size(80.dp)
		                        .padding(8.dp),
                        contentScale = ContentScale.Fit
                )
                Column(
                        modifier = Modifier
		                        .weight(1f)
		                        .padding(8.dp)
                ) {
                    Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                    )
                }
                BadgeIcon(badgeNumberText = project.maxBadges.toString())
            }
            Text(
                    text = "Határidő: ${project.deadline}",
                    style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                    text = "Kategória: ${project.categoryEnum}",
                    style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}