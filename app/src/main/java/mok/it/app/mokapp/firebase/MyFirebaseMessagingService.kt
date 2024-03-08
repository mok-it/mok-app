package mok.it.app.mokapp.firebase

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService
import java.util.Calendar

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"

        fun sendNotificationToUsersById(
            title: String,
            messageBody: String,
            adresseeUserIdList: List<String>,
        ) {
            //TODO remove duplicates from adresseeUserIdList

            require(adresseeUserIdList.size <= 10)
            { "too many users to send notification to (the limit is 10)" }

            Firebase.firestore.collection(Collections.USERS)
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
            adresseeUserList.forEach { addresseeUser ->
                Log.d(TAG, "fcmtoken: ${addresseeUser.fcmToken}")
                Log.d(TAG, "sending notification to ${addresseeUser.name}")

                FirebaseMessaging.getInstance().send(
                    RemoteMessage.Builder(addresseeUser.fcmToken)
                        .setMessageId(generateMessageId())
                        .addData("title", title)
                        .addData("message", messageBody)
                        .build()
                )
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
        UserService.updateFcmToken(token)
    }
}