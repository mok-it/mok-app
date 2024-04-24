package mok.it.app.mokapp.compose.parameterproviders

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import mok.it.app.mokapp.model.Project

class ProjectParamProvider : PreviewParameterProvider<Project> {
    override val values = sequenceOf(
        Project(
            name = "Appfejlesztés 2",
            description = "Ez egy nagyon hosszú leírás, amiben mindenféle fontos dolgok szerepelnek.",
            category = "IT",
        ),
    )
}