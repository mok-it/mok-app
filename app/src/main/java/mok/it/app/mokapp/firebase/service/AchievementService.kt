package mok.it.app.mokapp.firebase.service

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User

object AchievementService {
    fun getAchievements(): Flow<List<Achievement>> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS)
            .snapshots()
            .map { s ->
                s.toObjects(Achievement::class.java)
            }
    }

    fun getAchievement(id: String): Flow<Achievement> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS).document(id)
            .snapshots()
            .map { s ->
                s.toObject(Achievement::class.java)
                    ?: Achievement() //TODO: more sophisticated error handling
            }
    }

    fun grantAchievement(achievement: Achievement, user: User, level: Int) {
        val userDocRef = Firebase.firestore.collection(Collections.USERS).document(user.documentId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                val ownedAchievements =
                    document.get("achievements") as? MutableMap<String, Int> ?: mutableMapOf()
                ownedAchievements[achievement.id] = level
                userDocRef.update("achievements", ownedAchievements)
            }
    }

    fun getOwners(achievementId: String): Flow<List<User>> {
        return Firebase.firestore.collection(Collections.USERS)
            .where(Filter.greaterThan("achievements.$achievementId", 0))
            .snapshots()
            .map { s ->
                s.toObjects(User::class.java)
            }
    }
}