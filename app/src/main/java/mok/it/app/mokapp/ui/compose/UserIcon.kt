package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.feature.project_detail.DetailsFragmentDirections
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.User

@Composable
fun UserIcon(
    navController: NavController,
    user: User,
    modifier: Modifier = Modifier,
    enableOnClick: Boolean = true,
) {
    val navigationDestination = if (userModel.documentId == user.documentId)
        DetailsFragmentDirections.actionGlobalProfileFragment()
    else
        DetailsFragmentDirections.actionGlobalMemberFragment(user)

    AsyncImage(
        model = user.photoURL,
        contentDescription = "User Profile Picture",
        placeholder = painterResource(id = R.drawable.no_image_icon),
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .let { if (enableOnClick) it.clickable { navController.navigate(navigationDestination) } else it }
    )
}