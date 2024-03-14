package mok.it.app.mokapp.fragments.viewmodels

import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.model.RewardRequest
import mok.it.app.mokapp.model.User
import java.util.Date

class RewardRequestListViewModel: ViewModel() {

    val rewardRequests = arrayOf(
        RewardRequest( "", "0", "0", "11", Date() ),
        RewardRequest( "", "1", "1", "2", Date() ),
        RewardRequest( "", "0", "1", "3", Date() ),
        RewardRequest( "", "0", "0", "5", Date() ),
        RewardRequest( "", "1", "0", "16", Date() ),
        RewardRequest( "", "1", "0", "19", Date() ),
    )

    private val users = arrayOf(
        User(name = "Példa Béla"),
        User( name = "Teszt Elek")
    )

    private val rewards = arrayOf(
        Reward(
            name = "Hangszóró",
            icon = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/Piktogramm_%20hangsz%C3%B3r%C3%B3.png?alt=media&token=ba598a93-5807-4a16-b61d-346a087ab099"
        ),
        Reward(
            name = "Kitűző",
            icon = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/Piktogramm_%20kit%C5%B1z%C5%91.png?alt=media&token=e25d4ad8-45c3-4997-bad8-776e05190f7c"
        )
    )

    fun getUserById( id: String ) : User {
        return users[ id.toInt() ]
    }

    fun getRewardById( id: String ) : Reward{
        return rewards[ id.toInt() ];
    }

}