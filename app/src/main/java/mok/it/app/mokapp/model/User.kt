package mok.it.app.mokapp.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import mok.it.app.mokapp.model.enums.Role
import mok.it.app.mokapp.utility.Utility.TAG

/**
 * The user object that is stored in the Firestore database.
 * The fields of the class should exactly match the fields in Firestore DB.
 */
@Suppress("DEPRECATION")
@Parcelize
data class User(
        @DocumentId val documentId: String = "",

        val email: String = "",
        override val name: String = "",
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
        val achievements: MutableMap<String, Int> = HashMap(),
        @Deprecated("Use roleEnum instead") var role: String = "",
) : Parcelable, Searchable {
    var roleEnum: Role
        get() = try {
            Role.valueOf(role)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Invalid role: '$role' of user: $name, setting to ${Role.BASIC_USER}")
            Role.BASIC_USER
        }
        set(value) {
            role = value.name
        }

    fun roleAtLeast(role: Role) = roleEnum.ordinal >= role.ordinal
}