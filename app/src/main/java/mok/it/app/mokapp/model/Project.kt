package mok.it.app.mokapp.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import mok.it.app.mokapp.utility.Utility.TAG
import mok.it.app.mokapp.utility.Utility.unaccent
import java.util.Date

/**
 * The Project object that is stored in the Firestore database.
 * The fields of the class should exactly match the fields in Firestore DB.
 *
 * @param category Can't be marked private, but do not use it.
 */

@Suppress("DEPRECATION")
@Parcelize
data class Project(

    @DocumentId
    val id: String = "",
    @Deprecated("Use categoryEnum instead")
    val category: String = "", // can't mark it private, but don't use it
    val created: Date = Date(),
    val creator: String = "",
    val deadline: Date = Date(),
    val description: String = "",
    val leaders: List<String> = ArrayList(),
    val icon: String = "",
    val members: List<String> = ArrayList(),
    val name: String = "",
    @get:PropertyName("overall_progress")
    val overallProgress: Int = 0,
    val mandatory: Boolean = false,
    val tasks: List<String> = ArrayList(),
    val comments: List<String> = ArrayList(),
    val maxBadges: Int = 1,
) : Parcelable {

    val categoryEnum: Category
        get() {
            return try {
                Category.valueOf(category.replace(" ", "").uppercase().unaccent())
            } catch (e: Exception) {
                Log.e(TAG, "Category not found: $category")
                Category.UNIVERZALIS
            }
        }
}