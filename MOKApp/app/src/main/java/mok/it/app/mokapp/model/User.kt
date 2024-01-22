package mok.it.app.mokapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import mok.it.app.mokapp.model.Category.Companion.toCategory

//the fields of the class should exactly match the fields in Firestore DB
@Parcelize
data class User(
    @DocumentId
    val documentId: String = "",

    val admin: Boolean = false,
    val categories: List<String> = ArrayList(), // can't mark it private, but don't use it
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
    val points: Int = 0,
    val fcmTokens: List<String> = ArrayList(),
    val nickname: String = "",
    val projectBadges: Map<String, Int> = HashMap()
) : Parcelable {
    fun generateCategories() {
        categoryList = categories.map { it.toCategory() }.toMutableList()
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}