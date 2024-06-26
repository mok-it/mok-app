package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.User

@Composable
fun UserCard(
    user: User,
    navController: NavController,
    enableOnClick: Boolean = true,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            UserIcon(
                navController = navController, user = user, modifier = Modifier
                    .size(100.dp),
                enableOnClick = enableOnClick
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
