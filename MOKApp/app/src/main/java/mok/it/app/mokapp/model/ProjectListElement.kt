package mok.it.app.mokapp.model

import android.media.Image
import com.google.firebase.firestore.DocumentId

//the fields of the class should exactly match the fields in Firestore DB
data class ProjectListElement(

    @DocumentId
    val id: String = "",

    val name: String = "",
    val description: String = "",
    val icon: String = ""
)
