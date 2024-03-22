package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.utility.Utility.TAG
import java.util.Date

object RewardsService {
    fun getRewardsQuery() =
        Firebase.firestore.collection(Collections.rewards)
            .orderBy("price", Query.Direction.ASCENDING)

    fun acceptRewardRequest(reward: Reward, onComplete: () -> Unit) {
        val request = hashMapOf(
            "user" to FirebaseUserObject.userModel.documentId,
            "reward" to reward.documentId,
            "price" to reward.price,
            "created" to Date()
        )

        // substract 1 from the quantity of the reward
        val rewardRef =
            Firebase.firestore.collection(Collections.rewards).document(reward.documentId)
        val newQuantity = reward.quantity - 1
        rewardRef.update("quantity", newQuantity)


        Firebase.firestore.collection(Collections.rewardrequests).add(request)
            .addOnSuccessListener { documentRef ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentRef.id}")
                onComplete.invoke()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        val userRef =
            Firebase.firestore.collection(Collections.users)
                .document(FirebaseUserObject.userModel.documentId)
        userRef.update(
            "requestedRewards", FieldValue.arrayUnion(reward.documentId),
            "points", FieldValue.increment(-1 * reward.price.toDouble())
        )
            .addOnCompleteListener {
                Log.d(TAG, "Reward added to requested")
            }
    }
}
