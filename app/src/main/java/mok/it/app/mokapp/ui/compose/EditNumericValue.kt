package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun EditNumericValue(value: Int, name: String, onValueChange: (Int) -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onValueChange(value - 1) }, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.RemoveCircle, contentDescription = "Decrease $name")
        }
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue: String ->
                onValueChange(newValue.toIntOrNull() ?: 0)
            },
            label = { Text(name) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(100.dp)
        )
        IconButton(onClick = { onValueChange(value + 1) }, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Increase $name")
        }
    }
}