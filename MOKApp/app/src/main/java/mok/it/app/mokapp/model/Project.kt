package mok.it.app.mokapp.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import mok.it.app.mokapp.utility.Utility.unaccent
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
@Parcelize
data class Project(

    @DocumentId
    val id: String = "",

    val category: String = "", // can't mark it private, but don't use it
    val created: Date = Date(),
    val creator: String = "",
    val deadline: Date = Date(),
    val description: String = "",
    val editors: List<String> = ArrayList(),
    val icon: String = "",
    val members: List<String> = ArrayList(),
    val name: String = "",
    val overall_progress: Int = 0,
    val mandatory: Boolean = false,
    val tasks: List<String> = ArrayList(),
    val comments: List<String> = ArrayList(),
    val value: Int = 1,
) : Parcelable {
    
    val categoryEnum: Category
        get() = Category.valueOf(category.uppercase().unaccent())
}