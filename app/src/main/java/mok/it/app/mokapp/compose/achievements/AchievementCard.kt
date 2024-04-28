package mok.it.app.mokapp.compose.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.ui.model.AchievementUi

@Composable
fun AchievementCard(achievement: AchievementUi, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.padding(8.dp, 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            AsyncImage(
                //TODO: use achievement.icon instead
                model = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/appfejleszt%C3%A9s.png?alt=media&token=5a404797-8139-4928-85db-f4c69d042db1",
                contentDescription = "under construction",
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 8.dp)
            )

            Column {
                Row {
                    //TODO: move strings to resource
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (achievement.ownedLevel == achievement.maxLevel) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_mark),
                            contentDescription = "megszerezve",
                            tint = colorResource(id = R.color.green_dark),
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (achievement.ownedLevel > 0) {
                        Spacer(modifier = Modifier.weight(1f))
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
                    } else if (achievement.mandatory) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_exclamation_mark),
                            contentDescription = "kötelező",
                            tint = colorResource(id = R.color.red_dark),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Text(
                    text = achievement.currentDescription,
                    softWrap = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
