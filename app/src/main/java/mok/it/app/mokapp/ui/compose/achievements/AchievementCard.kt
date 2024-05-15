package mok.it.app.mokapp.ui.compose.achievements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.R
import mok.it.app.mokapp.ui.compose.ImageItemCard
import mok.it.app.mokapp.ui.model.AchievementUi

@Composable
fun AchievementCard(achievement: AchievementUi, onClick: () -> Unit) {
    ImageItemCard(
        asyncImageModel = achievement.icon,
        asyncImageContentDescription = "acsi ikon",
        mainText = achievement.name,
        subText = achievement.currentDescription
            ?: LocalContext.current.getString(R.string.achievement_missing_description),
        icon = {
            when {
                achievement.ownedLevel == achievement.maxLevel -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_mark),
                        contentDescription = "megszerezve",
                        tint = colorResource(id = R.color.green_dark),
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
                        painter = painterResource(id = R.drawable.ic_exclamation_mark),
                        contentDescription = "kötelező",
                        tint = colorResource(id = R.color.red_dark),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        onClick = onClick
    )
}
