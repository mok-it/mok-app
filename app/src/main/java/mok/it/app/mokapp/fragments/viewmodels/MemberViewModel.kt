package mok.it.app.mokapp.fragments.viewmodels

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.firebase.service.UserService
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

class MemberViewModel : ViewModel() {
    data class BadgeData(val finishedProjectCount: Int, val finishedProjectBadgeSum: Int)

    fun getUserBadgeCountByCategory(user: User, category: Category): LiveData<BadgeData> {
        Log.d(TAG, "CALLED $category")
        val badgeData = MutableLiveData(BadgeData(0, 0))
        UserService.getBadgeSumForUserInCategory(
            userId = user.documentId,
            category = category.toString(),
            onComplete = { sum ->
                badgeData.value = BadgeData(0, sum)
                Log.d(TAG, sum.toString() + "$category")
            },
            onFailure = { exception ->
                // Handle failure
                Log.d(TAG, "Failed to retrieve sum of badges: $exception")
            }
        )
        return badgeData
    }

    fun loadImage(imageView: ImageView, imageURL: String): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}