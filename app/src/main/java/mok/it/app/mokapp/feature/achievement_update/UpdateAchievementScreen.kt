package mok.it.app.mokapp.feature.achievement_update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.feature.achievement_create.EditAchievementEvent
import mok.it.app.mokapp.feature.achievement_create.EditAchievementViewModel
import mok.it.app.mokapp.ui.compose.EditAchievement

@Composable
fun UpdateAchievementScreen(viewModel: EditAchievementViewModel, onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        EditAchievement(viewModel)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.onEvent(EditAchievementEvent.Update)
                    onNavigateBack()
                },
                modifier = Modifier
                    .width(120.dp)
                    .padding()
            ) {
                Text(text = "Módosítás")
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.width(120.dp)
            ) {
                Text(text = "Mégse")
            }
        }
//        Button(onClick = {
//            viewModel.onEvent(EditAchievementEvent.Delete)
//        }) {
//            Text(text = "Törlés")
//        }

    }
}
