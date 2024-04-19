package mok.it.app.mokapp.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
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

    val email: String = "",
    val name: String = "",
    val photoURL: String = "",
    val phoneNumber: String = "",
    val requestedRewards: List<String> = ArrayList(),
    /**
     * The total number of badges the user has earned in the current season. It is calculated by a cloud function, so do not modify.
     */
    val allBadges: Int = 0,
    /**
     * The number of badges the user has earned and didn't spend in the current season. It is calculated by a cloud function, so do not modify.
     */
    val remainingBadges: Int = 0,
    val fcmToken: String = "",
    val nickname: String = "",
    val projectBadges: MutableMap<String, Int> = HashMap(),

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