package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.m3.ChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState


@Preview
@Composable
fun SearchFieldPreview() {
    var searchQuery by remember { mutableStateOf("") }
    val state = rememberChipTextFieldState<Chip>()
    SearchField(searchQuery = searchQuery, chipState = state, onValueChange = { searchQuery = it })
}

@Composable
fun SearchField(
        searchQuery: String,
        chipState: ChipTextFieldState<Chip>,
        onValueChange: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    ChipTextField(
            state = chipState,
            value = searchQuery,
            onValueChange = { onValueChange(it) },
            onSubmit = { text -> Chip(text.trim()) },
            label = { Text("Keresés") },
            modifier = Modifier
		            .padding(8.dp)
		            .fillMaxWidth()
		            .focusRequester(focusRequester),
            leadingIcon = {
                Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        onValueChange("")
                        focusManager.clearFocus()
                    }) {
                        Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear Search"
                        )
                    }
                }
            },
    )
}