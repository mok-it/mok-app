package mok.it.app.mokapp.projects

import android.media.Image

//the fields of the class should exactly match the fields in Firestore DB
data class ProjectListElement(
    val name: String = "",
    val description: String = ""
)
