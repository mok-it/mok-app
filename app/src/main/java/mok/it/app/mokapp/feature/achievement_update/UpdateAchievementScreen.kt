package mok.it.app.mokapp.feature.achievement_update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import mok.it.app.mokapp.feature.achievement_create.EditAchievementEvent
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModel
import mok.it.app.mokapp.ui.compose.EditAchievement

@Composable
fun UpdateAchievementScreen(viewModel: EditAchievementViewModel) {
    Column {
        EditAchievement(viewModel)
        Row {
            Button(onClick = { viewModel.onEvent(EditAchievementEvent.Update) }) {
                Text(text = "Módosítás")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Mégse")
            }
        }
        Button(onClick = {
            viewModel.onEvent(EditAchievementEvent.Delete)
        }) {
            Text(text = "Törlés")
        }

    }
}
