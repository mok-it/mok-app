package mok.it.app.mokapp.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mok.it.app.mokapp.model.Stamp

@Composable
fun StampCard(
    stamp: Stamp,
    amount: Int = 1,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ){
        if( amount > 1 ) {
            BadgedBox(badge = { Badge { Text(amount.toString()) } }) {
                AsyncImage(model = stamp.icon, contentDescription = null)
            }
        } else {
            AsyncImage(model = stamp.icon, contentDescription = null)
        }
    }
}

@Preview
@Composable
fun StampCardPreview(){
    val stamp = Stamp( "", "Teszt", "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/Feladatsor_%20Ellen%C5%91rz%C5%91.png?alt=media&token=315f5fce-39f8-4f21-bd61-0cada8d9fd5d")
    StampCard(stamp = stamp, 3)
}