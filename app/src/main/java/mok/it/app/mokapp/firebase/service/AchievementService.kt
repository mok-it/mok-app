package mok.it.app.mokapp.firebase.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections

object AchievementService {
    fun getAchievements(): LiveData<List<Achievement>> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS)
            .snapshots()
            .map { s ->
                s.toObjects(Achievement::class.java)
            }
            .asLiveData()
    }
}