package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.utility.Utility.TAG

object AchievementService {
    @ExperimentalCoroutinesApi
    fun getAchievements(): Flow<List<Achievement>> {
        return callbackFlow {
            Firebase.firestore.collection(Collections.ACHIEVMENTS)
                .addSnapshotListener { querySnapshot, excepiton ->
                    if (excepiton != null) {
                        Log.e(TAG, "Failed to retrieve achievements: $excepiton")
                        close(excepiton)
                        return@addSnapshotListener
                    }
                    val achievementsList = querySnapshot?.documents?.mapNotNull { document ->
                        document.toObject(Achievement::class.java)
                    } ?: emptyList()
                    trySend(achievementsList)
                }
            awaitClose()
        }
    }
}