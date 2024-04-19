package mok.it.app.mokapp.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import mok.it.app.mokapp.model.enums.Role

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

    @Deprecated("This is no longer in use; use role instead")
    val admin: Boolean = false,
    @Deprecated("This is no longer in use; use projectBadges instead")
    val collectedBadges: List<String> = ArrayList(),
    val email: String = "",
    @Deprecated("This is no longer in use; use projectBadges instead")
    val joinedBadges: List<String> = ArrayList(),
    val name: String = "",
    @get:PropertyName("isCreator")
    @Deprecated("This is no longer in use; use role instead")
    val isCreator: Boolean = false,
    val photoURL: String = "",
    val phoneNumber: String = "",
    val requestedRewards: List<String> = ArrayList(),
    val badges: Int = 0,
    val fcmToken: String = "",
    val nickname: String = "",
    val projectBadges: MutableMap<String, Int> = HashMap(),
    val points: Int = 0,

    @Deprecated("Use roleEnum instead")
    var role: String = "",
) : Parcelable {
    var roleEnum: Role
        get() = Role.valueOf(role)
        set(value) {
            role = value.name
        }

    fun roleAtLeast(role: Role) = roleEnum.ordinal >= role.ordinal
}