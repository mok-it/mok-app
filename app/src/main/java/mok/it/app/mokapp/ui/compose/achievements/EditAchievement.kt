package mok.it.app.mokapp.ui.compose.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.feature.achievement_create.EditAchievementEvent
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModel

@Composable
fun EditAchievement(viewModel: EditAchievementViewModel) {
    val achievement by remember {
        viewModel.achievement
    }
    val levelDescriptions = remember {
        mutableStateListOf(*achievement.levelDescriptions.values.toTypedArray())
    }

    EditRow(
        "Név",
        achievement.name,
        onValueChange = { viewModel.onEvent(EditAchievementEvent.ChangeName(it)) }
    )
    EditListRow(
        levelDescriptions,
        onValueChange = { viewModel.onEvent(EditAchievementEvent.ChangeLevelDescriptions(it)) }
    )
    Spacer(modifier = Modifier.height(20.dp))
    EditRow(
        "Ikon URL-je",
        achievement.icon,
        onValueChange = { viewModel.onEvent(EditAchievementEvent.ChangeIcon(it)) }
    )
    EditBoolean(
        "Kötelező",
        achievement.mandatory,
        onValueChange = { viewModel.onEvent(EditAchievementEvent.ChangeMandatory(it)) }
    )


}

@Composable
fun EditRow(
    title: String,
    value: String,
    iconButton: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value,
                onValueChange = onValueChange,
                label = { Text(text = title) },
                modifier = Modifier
                    .weight(1f)
            )
            iconButton?.invoke()
        }
    }
}

@Composable
fun EditListRow(values: MutableList<String>, onValueChange: (List<String>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        values.forEachIndexed { i, t ->
            EditRow("${i + 1}. Szint leírása", t, iconButton = {
                if (values.size > 1) {
                    IconButton(
                        onClick = {
                            values.removeAt(i)
                            onValueChange(values as List<String>)
                        },
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Elem törlése"
                        )
                    }
                }
            }) { values[i] = it; onValueChange(values as List<String>) }
        }
        Button(
            onClick = { values.add(""); },
            shape = CircleShape,
            modifier = Modifier
                .padding(6.dp)
                .size(60.dp),
            contentPadding = PaddingValues(0.dp)

        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Szint hoazzáadása")
        }
    }
}

@Composable
fun EditBoolean(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value, onCheckedChange = onValueChange)
        Text(text = title)
    }
}