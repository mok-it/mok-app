package mok.it.app.mokapp.feature.achievement_create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import mok.it.app.mokapp.ui.compose.achievements.EditAchievement

@Composable
fun CreateAchievementScreen(viewModel: EditAchievementViewModel, onNavigateBack: () -> Unit) {
    Surface {
        Column {
            EditAchievement(viewModel)
            Row {
                Button(onClick = {
                    viewModel.onEvent(EditAchievementEvent.Insert)
                    onNavigateBack()
                }) {
                    Text(text = "Létrehozás")
                }
                Button(onClick = onNavigateBack) {
                    Text(text = "Mégse")
                }
            }
        }
    }
}
