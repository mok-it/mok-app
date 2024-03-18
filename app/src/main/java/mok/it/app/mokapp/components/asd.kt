package mok.it.app.mokapp.components
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//
//class asd {
//
//    data class User(val name: String, var isSelected: Boolean = false)
//
//    @Preview
//    @Composable
//    fun UserListScreen() {
//        val users = remember { mutableStateListOf(
//            User("Alice"),
//            User("Bob"),
//            User("Charlie")
//        )}
//
//        LazyColumn {
//            items(users) { user ->
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { // Make the row clickable
//                            val index = users.indexOf(user)
//                            users[index] = user.copy(isSelected = !user.isSelected)
//                        }
//                ) {
//                    Checkbox(
//                        checked = user.isSelected,
//                        onCheckedChange = null, // Disable checkbox's own click handling
//                        enabled = true // Optionally disable checkbox visual interaction
//                    )
//                    Text(text = user.name)
//                }
//            }
//        }
//    }
//}