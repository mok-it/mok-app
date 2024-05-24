package mok.it.app.mokapp.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mok.it.app.mokapp.R

@Composable
fun BadgeIcon(
    badgeNumberText: String,
    modifier: Modifier = Modifier,
    isGreen: Boolean = false,
    isEnabled: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        //modifier = Modifier.align(Alignment.Bottom)
    ) {
        Image(
            painter = painterResource(id = R.drawable.badgeicon),
            contentDescription = "Badge icon",
            colorFilter = when {
                !isEnabled -> ColorFilter.colorMatrix(ColorMatrix().apply {
                    setToSaturation(0f)
                })

                isGreen -> ColorFilter.tint(Color.Green)
                else -> null
            },
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = badgeNumberText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White
        )
    }
}
