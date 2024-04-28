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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import mok.it.app.mokapp.model.Achievement

@Composable
fun EditAchievement(achievement: MutableState<Achievement>) { //TODO: move data and business logic to viewmodel
    val a by remember { //TODO maybe possible to move up
        achievement
    }
    val levelDescriptions = remember {
        mutableStateListOf("")
    }
    EditRow(
        "Név",
        a.name,
        onValueChange = { s -> achievement.value = achievement.value.copy(name = s) }
    )
    EditListRow(
        levelDescriptions,
    )
    EditRow(
        "Ikon URL-je",
        achievement.value.icon,
        onValueChange = { s -> achievement.value = achievement.value.copy(icon = s) }
    )
    EditBoolean(
        "Kötelező",
        achievement.value.mandatory,
        onValueChange = { b -> achievement.value = achievement.value.copy(mandatory = b) }
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
fun <T> EditListRow(values: MutableList<T>) {
    Column {
        values.forEachIndexed { i, t ->
            EditRow("${i + 1}. Szint leírása", t, iconButton = {
                if (values.size > 1) {
                    IconButton(onClick = {
                        values.removeAt(i); Log.e(
                        "TAG",
                        "EditMapRow: ${values.size}"
                    )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Elem törlése"
                        )
                    }
                }
            }) { s -> values[i] = s as T }
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