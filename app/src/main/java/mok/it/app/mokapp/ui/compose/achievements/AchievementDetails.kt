package mok.it.app.mokapp.ui.compose.achievements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.AdminButton
import mok.it.app.mokapp.ui.compose.navigateToUser
import mok.it.app.mokapp.ui.compose.theme.ExtendedTheme
import mok.it.app.mokapp.ui.model.AchievementUi
import java.util.SortedMap

@Composable
fun AchievementDetails(
        achievement: AchievementUi,
        owners: SortedMap<Int, List<User>>,
        canModify: Boolean,
        navController: NavController,
        onEditClick: () -> Unit,
        onGrantClick: () -> Unit,
) {
    Surface {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                        model = achievement.icon,
                        contentDescription = "Acsi ikonja",
                        modifier = Modifier
		                        .size(100.dp)
		                        .padding(end = 18.dp)
                )
                Text(text = achievement.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                TopIcon(achievement)
            }
            Text(
                    text = achievement.currentDescription
                            ?: LocalContext.current.getString(R.string.achievement_missing_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
            )

            AdminButtonRow(canModify, onEditClick, onGrantClick)

            if (achievement.ownedLevel > 0) {
                OwnedStatusCard(achievement)
                OwnersGrid(achievement, owners, navController)
            } else if (achievement.mandatory) {
                MandatoryStatusCard()
            }
        }
    }
}


@Composable
private fun AdminButtonRow(canModify: Boolean, onEditClick: () -> Unit, onGrantClick: () -> Unit) {
    if (canModify) {
        Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
		                .fillMaxWidth()
		                .padding(vertical = 8.dp)
        ) {
            AdminButton(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Default.People,
                    contentDescription = "Edit levels",
                    onClick = onGrantClick
            )
            AdminButton(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit achievement",
                    onClick = onEditClick
            )
        }

    }
}

@Composable
private fun TopIcon(achievement: AchievementUi) {
    when {
        achievement.ownedLevel == achievement.maxLevel -> {
            Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "megszerezve",
                    tint = ExtendedTheme.colorScheme.success.color,
                    modifier = Modifier.size(40.dp),

                    )
        }

        achievement.ownedLevel > 0 -> {
            Text(
                    text = "${achievement.ownedLevel}/${achievement.maxLevel}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(2.dp)
            )
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
private fun OwnedStatusCard(achievement: AchievementUi) {
    StatusCard(
            colors = CardDefaults.cardColors(
                    containerColor = ExtendedTheme.colorScheme.success.colorContainer,
                    contentColor = ExtendedTheme.colorScheme.success.onColorContainer
            ),
            icon = {
                Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Acsi megszerezve",
                        tint = ExtendedTheme.colorScheme.success.color,
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
            colors = CardDefaults.cardColors(
                    containerColor = ExtendedTheme.colorScheme.warning.colorContainer,
                    contentColor = ExtendedTheme.colorScheme.warning.onColorContainer
            ),
            icon = {
                Icon(
                        imageVector = Icons.Filled.PriorityHigh,
                        contentDescription = "Kötelező acsi",
                        tint = ExtendedTheme.colorScheme.warning.color,
                        modifier = Modifier
                                .fillMaxSize()
                )
            },
            text = "Ennek az acsinak a megszerzése kötelező a szezonban."
    )
}

@Composable
private fun OwnersGrid(
        achievement: AchievementUi,
        ownersByLevel: SortedMap<Int, List<User>>,
        navController: NavController
) {
    val dropdownStates =
            remember {
                mutableStateMapOf<Int, Boolean>().apply {
                    achievement.levelDescriptions.keys.forEach { key ->
                        this[key] = false
                    }
                }
            }
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
        for ((level, description) in achievement.levelDescriptions) {
            item(
                    span = { GridItemSpan(maxLineSpan) },
            ) { LevelCard(dropdownStates, level, achievement.name, description) }

            if (dropdownStates[level] == true) { //comparing with true because value is nullable
                if (ownersByLevel[level].isNullOrEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                                text = "Az egyik tag sem tart ezen a szinten.",
                                style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    items(ownersByLevel[level] ?: listOf()) { user ->
                        OwnerCard(user, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(
        dropdownStates: SnapshotStateMap<Int, Boolean>,
        level: Int,
        name: String,
        description: String
) {

    Card(
            onClick = { dropdownStates[level] = !dropdownStates[level]!! },
            modifier = Modifier
		            .fillMaxWidth()
		            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                        text = if (dropdownStates.size == 1) name else "$level. Szint",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            when (dropdownStates[level]) {
                true -> Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "dropdown",
                        modifier = Modifier.size(30.dp)
                )

                false -> Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "dropdown",
                        modifier = Modifier.size(30.dp)
                )

                else -> {
                    Log.wtf("AchievementDetails", "Dropdown state is null")
                }
            }
        }
    }
}

@Composable
private fun OwnerCard(owner: User, navController: NavController) {
    Card(
            onClick = { navigateToUser(owner, navController) },
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