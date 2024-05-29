package mok.it.app.mokapp.ui.compose.achievements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.R
import mok.it.app.mokapp.ui.compose.ImageItemCard
import mok.it.app.mokapp.ui.compose.theme.ExtendedTheme
import mok.it.app.mokapp.ui.model.AchievementUi

@Composable
fun AchievementCard(
        achievement: AchievementUi,
        showCompleteOnFirstLevel: Boolean,
        onClick: (AchievementUi) -> Unit
) {
    ImageItemCard(
            asyncImageModel = achievement.icon,
            asyncImageContentDescription = "acsi ikon",
            mainText = achievement.name,
            subText = (if (showCompleteOnFirstLevel) achievement.firstDescription else achievement.currentDescription)
                    ?: LocalContext.current.getString(R.string.achievement_missing_description),
            icon = {
                when {
                    achievement.ownedLevel == achievement.maxLevel ||
                            (achievement.ownedLevel >= 1 && showCompleteOnFirstLevel) -> {
                        Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "megszerezve",
                                tint = ExtendedTheme.colorScheme.success.color,
                                modifier = Modifier.size(40.dp)
                        )
                    }

                    achievement.ownedLevel > 0 -> {
                        Card(
                                modifier = Modifier
		                                .wrapContentSize()
		                                .padding(6.dp)
                        ) {
                            Text(
                                    text = "${achievement.ownedLevel}/${achievement.maxLevel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    achievement.mandatory -> {
                        Icon(
                                imageVector = Icons.Filled.PriorityHigh,
                                contentDescription = "kötelező",
                                tint = ExtendedTheme.colorScheme.warning.color,
                                modifier = Modifier.size(40.dp)
                        )
                    }
                }
            },
            onClick = { onClick(achievement) }
    )
}
