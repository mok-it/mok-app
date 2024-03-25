package mok.it.app.mokapp.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import mok.it.app.mokapp.model.Category.Companion.toCategory

/**
 * The user object that is stored in the Firestore database.
 * The fields of the class should exactly match the fields in Firestore DB.
 *
 * @param categories Can't be marked private, but do not use it, use [categoryList] instead.
 */
@Suppress("DEPRECATION")
@Parcelize
data class User(
    @DocumentId
    val documentId: String = "",

    val admin: Boolean = false,
    @Deprecated("Use categoryList instead")
    val categories: List<String> = ArrayList(),
    @Exclude
    var categoryList: MutableList<Category> = ArrayList(),
    val collectedBadges: List<String> = ArrayList(),
    val email: String = "",
    val joinedBadges: List<String> = ArrayList(),
    val name: String = "",
    @get:PropertyName("isCreator")
    val isCreator: Boolean = false,
    val photoURL: String = "",
    val phoneNumber: String = "",
    val requestedRewards: List<String> = ArrayList(),
    val badges: Int = 0,
    val fcmToken: String = "",
    val nickname: String = "",
    val projectBadges: MutableMap<String, Int> = HashMap(),
    val points: Int = 0
) : Parcelable {
    fun generateCategories() {
        categoryList = categories.map { it.toCategory() }.toMutableList()
    }
}