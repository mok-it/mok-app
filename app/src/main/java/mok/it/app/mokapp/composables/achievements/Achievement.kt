package mok.it.app.mokapp.composables.achievements

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Achievement

@Composable
fun AchievementCard(owned: Boolean, achievement: Achievement /*onClick: () -> Unit*/) {
    Card(/*onClick = onClick*/) {
        Column (modifier = Modifier.fillMaxWidth()) {
            Row {
                //TODO: move strings to resource
//                AsyncImage(model = R.string.under_construction_project_icon, contentDescription = "under construction")
                Text(text = achievement.name)
                if (owned) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(painter = painterResource(id = R.drawable.ic_check_circle), contentDescription = "megszerezve")
                }
                else if (achievement.mandatory) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(painter = painterResource(id = R.drawable.ic_close_24), contentDescription = "kötelező")
                }
            }
            Text(text = achievement.description)
        }
    }
}