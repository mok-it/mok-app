package mok.it.app.mokapp.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.m3.ChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState
import com.google.android.material.chip.Chip
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.fragments.viewmodels.ProfileViewModel
import mok.it.app.mokapp.model.User

@Composable
fun UserCard(
    user: User,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = user.photoURL,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.nickname.ifEmpty { "Nincs becenév megadva" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.phoneNumber.ifEmpty { stringResource(R.string.no_phone_number) },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ProjectBadgeSummary(viewModel: ProfileViewModel) {
    val projectDataListState =
        viewModel.userBadgeDataInEachCategory.observeAsState().value?.sortedByDescending { it.finishedProjectBadgeSum }
            ?: emptyList()

    Column {
        HorizontalDivider()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.collectedBadgesSummary,
                    projectDataListState.sumOf { it.finishedProjectBadgeSum }
                ),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.collectedProjectsSummary,
                    projectDataListState.sumOf { it.finishedProjectBadgeSum }
                ),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(projectDataListState) { badgeData ->
                BadgeCard(badgeData)
            }
        }
    }
}

@Composable
fun BadgeIcon(badgeNumberText: String, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    Box(
        contentAlignment = Alignment.Center,
        //modifier = Modifier.align(Alignment.Bottom)
    ) {
        Image(
            painter = painterResource(id = R.drawable.badgeicon),
            contentDescription = "Badge icon",
            colorFilter = if (isEnabled) null else ColorFilter.colorMatrix(ColorMatrix().apply {
                setToSaturation(
                    0f
                )
            }),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = badgeNumberText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White
        )
    }
}

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

@Composable
private fun BadgeCard(badgeData: UserService.BadgeData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = badgeData.category.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                badgeData.finishedProjectBadgeSum.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

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
