package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import mok.it.app.mokapp.fragments.viewmodels.MemberViewModel
import mok.it.app.mokapp.fragments.viewmodels.RewardRequestListViewModel
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.model.RewardRequest
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.theme.AppTheme
import java.util.Date

class RewardRequestsListFragment : Fragment(){

    private val viewModel: RewardRequestListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        var rewardRequests = viewModel.rewardRequests
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    // In Compose world
                    LazyColumn {
                        items( rewardRequests.size ){ i ->
                            val rewardRequest = rewardRequests[i]
                            val user = viewModel.getUserById( rewardRequest.user )
                            val reward = viewModel.getRewardById( rewardRequest.reward )
                            
                            RewardRequestListItem(
                                user = user, 
                                reward = reward, 
                                price = rewardRequest.price, 
                                date = rewardRequest.created,
                            )
                            
                            if( i < rewardRequests.size - 1 ){
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RewardRequestListItem(
    user: User,
    reward: Reward,
    price: String,
    date: Date,
    buttonsEnabled: Boolean = false,
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
){
    val userName = if( user.nickname != "" ) user.nickname else user.name
    ListItem(
        headlineContent = { Text(text = "${reward.name} beváltva ${userName} által ${price} mancsért") },
        leadingContent = {
            AsyncImage(
                model = reward.icon,
                contentDescription = null,
                modifier = Modifier.size(75.dp))
        },
        supportingContent = { Text(text = "$date" )},
        trailingContent = {
            if( buttonsEnabled ) {
                Row {
                    IconButton(onClick = onAccept) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier
                                .background(Color.Green)
                                .size(40.dp),
                            tint = Color.White,
                        )
                    }
                    IconButton(onClick = onDecline) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier
                                .background(Color.Red)
                                .clip(RoundedCornerShape(5.dp))
                                .size(40.dp),
                            tint = Color.White,
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .wrapContentHeight(),
    )
}


@Preview(showBackground = true)
@Composable
fun RewardRequestListItemPreview(){
    val user = User(
        name = "Példa Béla",
        photoURL = "https://lh3.googleusercontent.com/a/ALm5wu237FEeNYT0kzhzBD-JL9Iigchflk2B4W5YlYIkXw=s96-c",
        nickname = "",
        points = 9,
    )
    val reward = Reward(
        name = "Hangszóró",
        price = 12,
        icon = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/Piktogramm_%20hangsz%C3%B3r%C3%B3.png?alt=media&token=ba598a93-5807-4a16-b61d-346a087ab099",
    )
    RewardRequestListItem(
        user = user,
        reward = reward,
        price ="15",
        date = Date()
        )
}