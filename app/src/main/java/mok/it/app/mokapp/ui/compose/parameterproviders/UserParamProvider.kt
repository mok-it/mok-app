package mok.it.app.mokapp.ui.compose.parameterproviders

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import mok.it.app.mokapp.model.User

class UserParamProvider : PreviewParameterProvider<User> {
    override val values = sequenceOf(
            User(
                    name = "Példa Pál",
            ),
    )
}