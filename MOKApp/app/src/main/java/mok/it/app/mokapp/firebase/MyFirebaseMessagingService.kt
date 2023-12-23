package mok.it.app.mokapp.firebase

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import java.util.Calendar

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"

        fun sendNotificationToUsersById(
            title: String,
            messageBody: String,
            adresseeUserIdList: List<String>,
        ) {
            return
            TODO("haven't been tested yet")
            require(adresseeUserIdList.size <= 10)
            { "too many users to send notification to (the limit is 10)" }

            Firebase.firestore.collection(Collections.users)
                .whereIn(FieldPath.documentId(), adresseeUserIdList)
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
            adresseeUserList: List<User>
        ) {
            return
            TODO("doesn't work correctly yet, npe")
            adresseeUserList.toHashSet().forEach { addresseeUser ->
                Firebase.firestore.collection(Collections.users).document(addresseeUser.documentId)
                    .get().addOnSuccessListener { document ->
                        val fcmToken = document["FCMtokens"] as List<*>
                        Log.d(TAG, "fcmtoken: ${fcmToken[0]}")
                        Log.d(TAG, "sending notification to ${document.get("name")}")

                        FirebaseMessaging.getInstance().send(
                            RemoteMessage.Builder(fcmToken[0] as String)
                                .setMessageId(generateMessageId())
                                .addData("title", title)
                                .addData("message", messageBody)
                                .build()
                        )

                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "failed to get user: ", exception)
                    }
            }
        }

        private fun generateMessageId(): String {
            Log.d(TAG, "generateMessageId: ${Calendar.getInstance().time} ${userModel.name}")
            return "${Calendar.getInstance().time} ${userModel.name}"
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            //TODO handle data payload
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        currentUser?.apply {
            Firebase.firestore.collection(Collections.users).document(this.uid)
                .update("FCMtokens", token)
                .addOnSuccessListener {
                    Log.d(TAG, "onNewToken: token uploaded to firestore")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "onNewToken: token upload failed", exception)
                }
        }
    }
}