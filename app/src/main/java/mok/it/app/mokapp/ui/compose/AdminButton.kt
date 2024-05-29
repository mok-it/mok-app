package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AdminButton(
        modifier: Modifier, imageVector: ImageVector,
        contentDescription: String,
        onClick: () -> Unit,
) {
    IconButton(
            modifier = modifier
		            .padding(horizontal = 8.dp, vertical = 8.dp)
		            .clip(RoundedCornerShape(8.dp))
		            .background(MaterialTheme.colorScheme.primary),
            onClick = {
                onClick()
            }) {
        Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.inversePrimary,
        )
    }
}
