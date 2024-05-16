package mok.it.app.mokapp.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageItemCard(
    asyncImageModel: Any?,
    asyncImageContentDescription: String? = null,
    mainText: String,
    subText: String,
    icon: @Composable () -> Unit,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = colors
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = asyncImageModel,
                contentDescription = asyncImageContentDescription,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = mainText,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            icon()
        }
    }
}

//@Composable
//fun ImageItemCard(
//    asyncImageModel: Any?,
//    asyncImageContentDescription: String? = null,
//    mainText: String,
//    subText: @Composable () -> Unit,
//    icon: @Composable () -> Unit,
//    colors: CardColors = CardDefaults.cardColors(),
//    onClick: () -> Unit
//) {
//    Card(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        shape = RoundedCornerShape(8.dp),
//        colors = colors
//    ) {
//        Row(
//            modifier = Modifier.padding(end = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            AsyncImage(
//                model = asyncImageModel,
//                contentDescription = asyncImageContentDescription,
//                modifier = Modifier
//                    .size(80.dp)
//                    .padding(8.dp),
//                contentScale = ContentScale.Fit
//            )
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//            ) {
//                Text(
//                    text = mainText,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(
//                    text = subText,
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//            icon()
//        }
//    }
//}
