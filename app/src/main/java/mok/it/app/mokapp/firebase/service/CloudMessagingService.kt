package mok.it.app.mokapp.firebase.service

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG

object CloudMessagingService : FirebaseMessagingService() {

    fun sendNotificationToUsersById(
        title: String,
        messageBody: String,
        adresseeUserIdList: List<String>,
    ) {
        val adresseeUserIds = adresseeUserIdList.distinct()

        require(adresseeUserIds.size <= 10)
        { "too many users to send notification to (the limit is 10)" }

        Firebase.firestore.collection(Collections.USERS)
            .whereIn(FieldPath.documentId(), adresseeUserIds)
            .get().addOnSuccessListener { documents ->
                val addresseeUserList = ArrayList<User>()
                for (document in documents) {
                    addresseeUserList.add(document.toObject(User::class.java))
                }
                sendNotificationToUsers(title, messageBody, addresseeUserList)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "failed to get users: ", exception)
            }
    }

    fun sendNotificationToUsers(
        title: String,
        messageBody: String,
        adresseeUserList: List<User>,
    ) {
        adresseeUserList.distinct().forEach { addresseeUser ->
            Log.d(
                TAG,
                "sending notification to ${addresseeUser.name}, FCM token: ${addresseeUser.fcmToken}"
            )
            val data = hashMapOf(
                "message" to hashMapOf(
                    "title" to title,
                    "body" to messageBody,
                    "icon" to "gs://mokapp-51f86.appspot.com/Feladatellenőrzés 16 feladat ellenőrzése.png",
                    "click_action" to "",
                ),
                "fcmToken" to addresseeUser.fcmToken
            )

            Firebase.functions
                .getHttpsCallable("sendNotification")
                .call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "Error calling cloud function", task.exception)
                    } else {
                        Log.d(TAG, "Notification sent successfully")
                    }
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //show it as a notification
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, it.body, Toast.LENGTH_LONG).show()
            }
        }

        //handle the data payload, if there is any
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            //this is where we should handle the data payload, if we want to
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        UserService.updateFcmToken(token)
    }
}