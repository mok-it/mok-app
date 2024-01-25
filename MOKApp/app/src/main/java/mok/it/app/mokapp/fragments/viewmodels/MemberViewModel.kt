package mok.it.app.mokapp.fragments.viewmodels

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.IUserService
import mok.it.app.mokapp.service.UserService

class MemberViewModel : ViewModel() {
    private val userService: IUserService = UserService
    data class BadgeData(val finishedProjectCount: Int, val finishedProjectBadgeSum: Int)

    fun getUserBadgeCountByCategory(user: User, category: Category): LiveData<BadgeData> {
        Log.d("MANCSAIM", "CALLED ${category.toString()}")
        val badgeData = MutableLiveData(BadgeData(0, 0))
        userService.getBadgeSumForUserInCategory(
            userId = user.documentId,
            category = category.toString(),
            onComplete = { sum ->
                var finishedProjectBadgeSum = badgeData.value?.finishedProjectBadgeSum ?: 0
                finishedProjectBadgeSum = sum
                badgeData.value = BadgeData(0, finishedProjectBadgeSum)
                Log.d("MANCSAIM",sum.toString() + "${category.toString()}")
            },
            onFailure = { exception ->
                // Handle failure
                Log.d("MANCSAIM","Failed to retrieve sum of badges: $exception")
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