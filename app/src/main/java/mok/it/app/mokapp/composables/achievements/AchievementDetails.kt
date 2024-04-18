package mok.it.app.mokapp.composables.achievements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.User

@Composable
fun AchievementDetails(
    achievement: Achievement,
    owned: Boolean,
    owners: List<User>
) {
//    val owned by remember { mutableStateOf(false) }
    Surface {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    //TODO: use achievement.icon instead
                    model = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/appfejleszt%C3%A9s.png?alt=media&token=5a404797-8139-4928-85db-f4c69d042db1",
                    contentDescription = "under construction",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 18.dp)
                )
                Text(text = achievement.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                TopIcon(owned, achievement.mandatory)
            }
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            if (owned) {
                OwnedStatusCard()
                OwnersGrid(owners)
            } else if (achievement.mandatory) {
                MandatoryStatusCard()
            }
        }
    }
}

@Composable
private fun TopIcon(owned: Boolean, mandatory: Boolean) {
    if (owned) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check_mark),
            contentDescription = "megszerezve",
            tint = colorResource(id = R.color.green_dark),
            modifier = Modifier.size(40.dp)
        )
    } else if (mandatory) {
        Icon(
            painter = painterResource(id = R.drawable.ic_exclamation_mark),
            contentDescription = "kötelező",
            tint = colorResource(id = R.color.red_dark),
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun StatusCard(colors: CardColors, icon: @Composable () -> Unit, text: String) {
    Card(
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(6.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                icon()
            }
//            icon()
//            Icon(
//                painter = painterResource(id = R.drawable.ic_trophy),
//                contentDescription = "Acsi megszerezve",
//                tint = colorResource(id = R.color.green_dark)
//            )
            Text(
                text = text, modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun OwnedStatusCard() {
    StatusCard(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green_light)),
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = "Acsi megszerezve",
                tint = colorResource(id = R.color.green_dark),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        },
        text = "Szép munka! Ezt az acsit már megszerezted."
    )
//    Card(
//        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green_light)),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(4.dp)
//    ) {
//        Column {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_trophy),
//                contentDescription = "Acsi megszerezve",
//                tint = colorResource(id = R.color.green_dark)
//            )
//            Text(text = "Szép munka! Ezt az acsit már megszerezted.")
//        }
//    }
}

@Composable
private fun MandatoryStatusCard() {
    StatusCard(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.red_light)),
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_exclamation_mark),
                contentDescription = "Kötelező acsi",
                tint = colorResource(id = R.color.red_dark),
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        text = "Ennek az acsinak a megszerzése kötelező a szezonban."
    )
//    Card(colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.red_light))) {
//        Column {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_exclamation_mark),
//                contentDescription = "Kötelező acsi",
//                tint = colorResource(id = R.color.red_dark)
//            )
//            Text(text = "Ennek az acsinak a megszerzése kötelező a szezonban.")
//        }
//    }
}

@Composable
private fun OwnersGrid(owners: List<User>) {
    Log.d("OwnersGrid", "Owners: $owners")
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 220.dp)) {
        items(owners) { owner ->
            Card {
                Text(text = "Owner: ${owner.name}")
            }
        }
    }
}