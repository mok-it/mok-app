package mok.it.app.mokapp.firebase.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User

object AchievementService {
    fun getAchievements(): LiveData<List<Achievement>> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS)
            .snapshots()
            .map { s ->
                s.toObjects(Achievement::class.java)
            }
            .asLiveData()
    }

    fun getAchievement(id: String): LiveData<Achievement> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS).document(id)
            .snapshots()
            .map { s ->
                s.toObject(Achievement::class.java)
                    ?: Achievement() //TODO: more sophisticated error handling
            }
            .asLiveData()
    }

    fun grantAchievement(achievement: Achievement, user: User) {
        val userDocRef = Firebase.firestore.collection(Collections.USERS).document(user.documentId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                val achievements =
                    document.get("achievements") as? MutableList<String> ?: mutableListOf()
                if (!achievements.contains(achievement.id)) {
                    val achievementDocRef = Firebase.firestore.collection(Collections.ACHIEVMENTS)
                        .document(achievement.id)
                    achievementDocRef.get()
                        .addOnSuccessListener { achievementDoc ->
                            val owners =
                                achievementDoc.get("owners") as? MutableList<String>
                                    ?: mutableListOf()
                            achievements.add(achievement.id)
                            userDocRef.update("achievements", achievements)
                            owners.add(user.documentId)
                            achievementDocRef.update("owners", owners)
                        }
                }
            }
    }
}