package mok.it.app.mokapp.utility

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User

class Config {
    companion object {
        fun getSeason(): String? {
            var cm: ConfigModel? = null
            Firebase.firestore.collection("config")
                .document("config")
                .get().addOnSuccessListener { document ->
                    cm = document.toObject(ConfigModel::class.java)
                }
            return cm?.season
        }
    }
}

data class ConfigModel(
    @DocumentId
    val id: String = "",

    val season: String = "",
)