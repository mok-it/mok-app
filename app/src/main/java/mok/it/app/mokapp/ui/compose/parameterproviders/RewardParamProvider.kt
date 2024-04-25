package mok.it.app.mokapp.ui.compose.parameterproviders

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import mok.it.app.mokapp.model.Reward

class RewardParamProvider : PreviewParameterProvider<Reward> {
    override val values = sequenceOf(
        Reward(
            name = "Hangszóró",
            price = 10,
            icon = "https://picsum.photos/200",
            quantity = 100,
        ),
    )
}