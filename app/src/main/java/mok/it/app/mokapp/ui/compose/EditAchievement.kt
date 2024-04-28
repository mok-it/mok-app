package mok.it.app.mokapp.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import mok.it.app.mokapp.feature.achievement_create.EditAchievementEvent
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModel

@Composable
fun EditAchievement(viewModel: EditAchievementViewModel) {
    val achievement by remember {
        viewModel.achievement
    }
    val levelDescriptions = remember {
        mutableStateListOf("")
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
fun <T> EditRow(
    title: String,
    value: T,
    iconButton: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    Row {
        TextField(value.toString(), onValueChange = onValueChange, label = { Text(text = title) })
        if (iconButton != null) {
            iconButton()
        }
    }
}

@Composable
fun <T> EditListRow(values: MutableList<T>, onValueChange: (List<String>) -> Unit) {
    Column {
        values.forEachIndexed { i, t ->
            EditRow("${i + 1}. Szint leírása", t, iconButton = {
                if (values.size > 1) {
                    IconButton(onClick = {
                        values.removeAt(i)
                        onValueChange(values as List<String>)
                        Log.e("TAG", "EditMapRow: ${values.size}")

                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Elem törlése"
                        )
                    }
                }
            }) { values[i] = it as T; onValueChange(values as List<String>) }
        }

    }
    Button(onClick = {
        values.add("" as T);
        Log.e(
            "TAG", "EditMapRow: ${values.size}",
        )
    }) {//TODO: remove unchecked cast
        Icon(imageVector = Icons.Default.Add, contentDescription = "Szint hoazzáadása")
    }
}

@Composable
fun EditBoolean(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row {

        Checkbox(checked = value, onCheckedChange = onValueChange)
        Text(text = title)
    }
}