package mok.it.app.mokapp.feature.achievement_create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.ui.compose.EditAchievement

@Composable
fun CreateAchievementScreen(achievement: MutableState<Achievement>) {
    Column {
        EditAchievement(achievement)
        Row {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Mentés")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Mégse")
            }
        }

    }
}
