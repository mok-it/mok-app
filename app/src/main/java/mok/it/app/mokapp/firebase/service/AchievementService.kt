package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.utility.Utility.TAG

object AchievementService {
    @ExperimentalCoroutinesApi
    fun getAchievements(): Flow<List<Achievement>> {
        Log.w(TAG, "getAchievements: running", ) //TODO delete
        return callbackFlow {
        Firebase.firestore.collection(Collections.ACHIEVMENTS).get()
            .addOnSuccessListener { querySnapshot ->

                val achievementsList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Achievement::class.java)
                }
                trySend(achievementsList)





//                for (document in querySnapshot.documents) {
//                    Log.w(TAG, "getAchievements: got an achievement", ) //TODO delete
//                    val achievement = document.toObject(Achievement::class.java)
//                    achievement?.let {
//                        achievementsList.add(it)
//                    }
//                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve achievements: $exception")
                close(exception)
            }
awaitClose()
//        return achievementsList
    }}
}