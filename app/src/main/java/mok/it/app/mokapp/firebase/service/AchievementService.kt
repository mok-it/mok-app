package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Achievement
import mok.it.app.mokapp.model.AchievementEntity
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

object AchievementService {
    fun getAchievements(): Flow<List<Achievement>> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS)
            .snapshots()
            .map { s ->
                s.toObjects(AchievementEntity::class.java).map { it.toAchievement() }
            }
    }

    fun getAchievement(id: String): Flow<Achievement> {
        return Firebase.firestore.collection(Collections.ACHIEVMENTS).document(id)
            .snapshots()
            .map { s ->
                s.toObject(AchievementEntity::class.java)?.toAchievement()
                    ?: AchievementEntity().toAchievement() //TODO: more sophisticated error handling
            }
    }

    fun grantAchievement(achievementId: String, user: User, level: Int) {
        val userDocRef = Firebase.firestore.collection(Collections.USERS).document(user.documentId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                val ownedAchievements =
                    document.get("achievements") as? MutableMap<String, Int> ?: mutableMapOf()
                ownedAchievements[achievementId] = level
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

    fun insertAchievement(achievement: AchievementEntity) {
        val achievement = achievement.copy(id = "")
        Firebase.firestore.collection(Collections.ACHIEVMENTS)
            .add(achievement)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    fun updateAchievement(achievement: AchievementEntity) { //TODO: update achievements and users in a single transaction
        Firebase.firestore.collection(Collections.ACHIEVMENTS).document(achievement.id)
            .set(achievement)
            .addOnSuccessListener {
                val maxLevel = achievement.levelDescriptions.keys.maxOrNull()?.toInt()
                    ?: throw IllegalArgumentException("Achievement has no levels, which should not be possible.")
                Firebase.firestore.collection(Collections.USERS)
                    .where(Filter.greaterThan("achievements.${achievement.id}", maxLevel))
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = Firebase.firestore.batch()
                        for (document in querySnapshot.documents) {
                            val userRef = Firebase.firestore.collection(Collections.USERS)
                                .document(document.id)
                            batch.update(userRef, "achievements.${achievement.id}", maxLevel)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d(TAG, "Achievement ${achievement.id} successfully updated!")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error updating Achievement ${achievement.id}", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error getting users for Achievement ${achievement.id}", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating Achievement ${achievement.id}", e)
            }
    }

    fun deleteAchievement(id: String) {
        Firebase.firestore.collection(Collections.ACHIEVMENTS).document(id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Achievement $id successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting Achievement $id", e)
            }
    }
}