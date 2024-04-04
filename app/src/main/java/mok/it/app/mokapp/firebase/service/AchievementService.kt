package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.utility.Utility.TAG

object AchievementService {
    fun getAchievements(): LiveData<List<Achievement>> {
        return liveData(Dispatchers.IO) {
            val achievementsLiveData = MutableLiveData<List<Achievement>>()
            Firebase.firestore.collection(Collections.ACHIEVMENTS)
                .addSnapshotListener { querySnapshot, excepiton ->
                    if (excepiton != null) {
                        Log.e(TAG, "Failed to retrieve achievements: $excepiton")
                        return@addSnapshotListener
                    }
                    val achievementsList = querySnapshot?.documents?.mapNotNull { document ->
                        document.toObject(Achievement::class.java)
                    } ?: emptyList()
                    achievementsLiveData.postValue(achievementsList)
                }
            emitSource(achievementsLiveData)
        }
    }
}