package mok.it.app.mokapp.fragments.viewmodels

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

class MemberViewModel : ViewModel() {
    data class BadgeData(val finishedProjectCount: Int, val finishedProjectBadgeSum: Int)

    fun getUserBadgeCountByCategory(user: User, category: Category): LiveData<BadgeData> {
        val badgeData = MutableLiveData(BadgeData(0, 0))

        user.collectedBadges.chunked(10).let {
            it.forEach { batch ->
                Firebase.firestore.collection(Collections.badges)
                    .whereEqualTo("category", category.toString())
                    .whereIn(FieldPath.documentId(), batch)
                    .get()
                    .addOnSuccessListener { documents ->
                        var finishedProjectCount = badgeData.value?.finishedProjectCount ?: 0
                        var finishedProjectBadgeSum = badgeData.value?.finishedProjectBadgeSum ?: 0

                        for (document in documents) {
                            finishedProjectCount++
                            finishedProjectBadgeSum += document.getDouble("value")?.toInt() ?: 0
                        }

                        badgeData.value = BadgeData(finishedProjectCount, finishedProjectBadgeSum)
                    }
            }
        }

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