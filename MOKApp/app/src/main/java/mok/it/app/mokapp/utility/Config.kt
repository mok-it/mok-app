package mok.it.app.mokapp.utility

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections

object Config {
    fun setSeason(onSuccess: () -> Unit) {
        Firebase.firestore.collection("config")
            .document("config")
            .get().addOnSuccessListener { document ->
                document.toObject(ConfigModel::class.java)?.season.let {
                    Collections.projects = "projects$it"
                    onSuccess()
                }
            }
    }
}

data class ConfigModel(
    @DocumentId
    val id: String = "",

    val season: String = "",
)