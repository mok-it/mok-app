package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mok.it.app.mokapp.model.User

@Composable
fun UserRow(
        user: User,
        isSelected: Boolean,
        navController: NavController,
        onCheckedChange: (Boolean) -> Unit,
) {
    Row(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable {
                onCheckedChange(!isSelected)
            }
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        UserIcon(user = user, navController = navController)
        Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
        )
        Checkbox(
                checked = isSelected,
                onCheckedChange = null // click is handled on whole row instead
        )
    }
}