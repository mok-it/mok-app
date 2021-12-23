package mok.it.app.mokapp.model

import com.google.firebase.firestore.DocumentId
import java.util.*

//the fields of the class should exactly match the fields in Firestore DB
data class Project(

    @DocumentId
    val id: String = "",

    val created: Date,
    val creator: String,
    val deadline: Date,
    val description: String = "",
    val editors: List<String>,
    val icon: String = "",
    val members: List<String>,
    val name: String = "",
    val overall_progress: Int,
    val tasks: List<String>,
)
