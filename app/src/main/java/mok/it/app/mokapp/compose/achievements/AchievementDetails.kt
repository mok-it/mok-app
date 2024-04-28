package mok.it.app.mokapp.compose.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.feature.achievement_detail.AchievementDetailsViewModel
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.model.AchievementUi
import java.util.SortedMap
import kotlin.math.roundToInt

@Composable
fun AchievementDetails(
    achievement: AchievementUi,
    owners: SortedMap<Int, List<User>>,
    vm: AchievementDetailsViewModel //TODO delete
) {
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
                TopIcon(achievement) { vm.grant(achievement.id) }
            }
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            if (achievement.ownedLevel > 0) {
                OwnedStatusCard(achievement)
                OwnersGrid(owners)
            } else if (achievement.mandatory) {
                MandatoryStatusCard()
            }
        }
    }
}

@Composable //TODO delete végtelen gány
private fun TopIcon(achievement: AchievementUi, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        if (achievement.ownedLevel == achievement.maxLevel) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_mark),
                contentDescription = "megszerezve",
                tint = colorResource(id = R.color.green_dark),
                modifier = Modifier.size(40.dp),

                )
        } else if (achievement.ownedLevel > 0) {
            Text(
                text = "${achievement.ownedLevel}/${achievement.maxLevel}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(2.dp)
            )
        } else if (achievement.mandatory) {
            Icon(
                painter = painterResource(id = R.drawable.ic_exclamation_mark),
                contentDescription = "kötelező",
                tint = colorResource(id = R.color.red_dark),
                modifier = Modifier.size(40.dp)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.about_icon_email),
                contentDescription = "megszerezve",
                tint = colorResource(id = R.color.green_dark),
                modifier = Modifier.size(40.dp)
            )

        }
    }
}

@Composable
private fun StatusCard(
    colors: CardColors,
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
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
            Text(
                text = text, modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun OwnedStatusCard(achievement: AchievementUi) { //TODO: delete param
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
        text = when {
            (achievement.ownedLevel == achievement.maxLevel && achievement.maxLevel == 1) ->
                "Szép munka! Ezt az acsit már megszerezted."

            (achievement.ownedLevel == achievement.maxLevel && achievement.maxLevel > 1) ->
                "Szép munka! Ennek az acsinak mind a(z) ${achievement.maxLevel} szintjét megszerezted."

            else ->
                "Az acsi ${achievement.maxLevel} szintjéből már ${achievement.ownedLevel} szintet sikerült megszerezned. Csak így tovább!"
        }
    )
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
}

@Composable
private fun OwnersGrid(ownersByLevel: SortedMap<Int, List<User>>) {
    val dropdownStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            ownersByLevel.keys.forEach { key ->
                this[key] = false
            }
        }
    }
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
        for ((level, owners) in ownersByLevel) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                Card(onClick = { dropdownStates[level] = !dropdownStates[level]!! }) {
                    Text(
                        text = "Level $level",
                    )
                }
            }
            if (dropdownStates[level] == true) { //comparing with true because value is nullable
                items(owners) { user ->
                    OwnerCard(user)
                }
            }
        }
    }
}

@Composable
private fun OwnerCard(owner: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = owner.photoURL,
                contentDescription = "User's profile picture",
                modifier = Modifier
                    .size(50.dp)
                    .padding(6.dp)
                    .clip(CircleShape)
            )
            Text(
                text = owner.name,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Layout() {
    val cardOpenStatus = remember { mutableStateOf(List(5) { false }) }

    LazyColumn {
        itemsIndexed(cardOpenStatus.value) { index, isOpen ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        cardOpenStatus.value =
                            cardOpenStatus.value.mapIndexed { i, b -> if (i == index) !b else b }
                    }
                    .padding(4.dp)
            ) {
                Column {
                    Text(text = "Card ${index + 1}", modifier = Modifier.padding(8.dp))


                    if (isOpen) {
                        val padding = 8
                        val x = LocalContext.current.resources.displayMetrics
                        val columns by remember { mutableIntStateOf((x.widthPixels / x.density).roundToInt() / 150) }
                        val width by remember { mutableIntStateOf(((x.widthPixels / x.density).roundToInt() - 24 - (padding * (columns + 1))) / columns) }
                        FlowRow(
                            maxItemsInEachRow = columns,
                            modifier = Modifier
                                .padding(0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(20) { boxIndex ->
                                Box(
                                    modifier = Modifier
                                        .width(width.dp)
                                        .padding(padding.dp)
                                        .background(Color.LightGray)
                                ) {
                                    Text(
                                        text = "Box #${boxIndex + 1}",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}