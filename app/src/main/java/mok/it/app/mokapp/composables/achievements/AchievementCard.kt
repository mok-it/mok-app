package mok.it.app.mokapp.composables.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Achievement

@Composable
fun AchievementCard(owned: Boolean, achievement: Achievement /*onClick: () -> Unit*/) {
    Card(/*onClick = onClick*/ modifier = Modifier.padding(8.dp, 4.dp)) {
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
                    if (owned) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_mark),
                            contentDescription = "megszerezve",
                            tint = colorResource(id = R.color.green_dark),
                            modifier = Modifier.size(40.dp)
                        )
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
                    text = achievement.description,
                    softWrap = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun cards() {
    val achievements = listOf(
        Achievement(
            name = "Önkéntesség",
            description = "Önkénteskedj 2 szabadtéri Medve matekversenyen",
            mandatory = true
        ),
        Achievement(
            name = "Feladatbeküldés",
            description = "Küldj be legalább 3 feladatot",
            mandatory = true
        ),
        Achievement(
            name = "Acsi",
            description = "Ez egy összetetteb acsi, aminek a megszerzéséhez több feladatot kell teljesítend. Először is",
            mandatory = false
        ),
    )
    LazyColumn {
        items(achievements) { achievement ->
            AchievementCard(owned = false, achievement = achievement)
        }
        item { AchievementCard(owned = true, achievement = achievements[0]) }
    }

}