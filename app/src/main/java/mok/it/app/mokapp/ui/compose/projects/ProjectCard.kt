package mok.it.app.mokapp.ui.compose.projects

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.ui.compose.BadgeIcon
import mok.it.app.mokapp.ui.compose.ImageItemCard

@Composable
fun ProjectCard(project: Project, onClick: (Project) -> Unit) {
    ImageItemCard(
        asyncImageModel = project.icon,
        asyncImageContentDescription = "Projekt ikon",
        mainText = project.name,
        subText = project.description,
        icon = { BadgeIcon(badgeNumberText = project.maxBadges.toString()) },
        colors = CardColors(
            containerColor = if (FirebaseUserObject.userModel.projectBadges.contains(project.id))
                Color(0xFF00FF00)
            else
                CardDefaults.cardColors().containerColor,
            contentColor = CardDefaults.cardColors().contentColor,
            disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
            disabledContentColor = CardDefaults.cardColors().disabledContentColor,
        ),
        onClick = { onClick(project) }
    )
}
