package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
data class Project(

    @DocumentId
    val id: String = "",

    val category: String = "",
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
)

fun getIconFileName(iconURL: String): String {
    // remove special characters
    val re = Regex("[^A-Za-z0-9 ]")
    val alphanum = re.replace(iconURL, "")
    // append file type
    return alphanum + ".png"
}