package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.utility.Utility.TAG
import java.util.Date

object RewardsService {
    fun getAllRewards(): Flow<List<Reward>> =
        Firebase.firestore.collection(Collections.REWARDS)
            .orderBy("price", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Reward::class.java)
            }

    fun requestReward(reward: Reward) {
        val request = hashMapOf(
            "user" to FirebaseUserObject.userModel.documentId,
            "reward" to reward.documentId,
            "price" to reward.price,
            "created" to Date()
        )

        // substract 1 from the quantity of the reward
        val rewardRef =
            Firebase.firestore.collection(Collections.REWARDS).document(reward.documentId)
        val newQuantity = reward.quantity - 1
        rewardRef.update("quantity", newQuantity)


        Firebase.firestore.collection(Collections.REWARDREQUESTS).add(request)
            .addOnSuccessListener { documentRef ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        val userRef =
            Firebase.firestore.collection(Collections.USERS)
                .document(FirebaseUserObject.userModel.documentId)
        userRef.update(
            "requestedRewards", FieldValue.arrayUnion(reward.documentId),
            "points", FieldValue.increment(-1 * reward.price.toDouble())
        )
            .addOnCompleteListener {
                Log.d(TAG, "Reward added to requested")
            }
    }

    fun updateReward(reward: Reward) {
        Firebase.firestore.collection(Collections.REWARDS).document(reward.documentId)
            .set(reward)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    fun deleteReward(reward: Reward) {
        Firebase.firestore.collection(Collections.REWARDS).document(reward.documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }

    fun getRewardsQuery() =
        Firebase.firestore.collection(Collections.REWARDS)
            .orderBy("price", Query.Direction.ASCENDING)
}
