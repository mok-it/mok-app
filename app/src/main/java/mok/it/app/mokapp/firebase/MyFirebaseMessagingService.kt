package mok.it.app.mokapp.firebase

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.Calendar


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"

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
            adresseeUserList: List<User>
        ) {
            adresseeUserList.distinct().forEach { addresseeUser ->
                Log.d(TAG, "fcmtoken: ${addresseeUser.fcmToken}")
                Log.d(
                    TAG,
                    "sending notification to ${addresseeUser.name}, FCM token: ${addresseeUser.fcmToken}"
                )

                val fcmServerKey =
                    "AAAAvxC7Nws:APA91bGO_wzATxqSbriJRPYYOeHAnI5KwUIkTrZjRGMKCBbqKOzs2AA7dMVkjEwyYoM4GSaje9F4h3maj6XEMvzyp1XQ2GLAy3kx8OBFwSF3Sb8Ra1h9hxKAsILHY9CCTAvYdHxD2VI3"
                val fcmToken = addresseeUser.fcmToken

                val client = OkHttpClient()
                val requestBody = """ 
    {
      "message": {
        "token": "$fcmToken",
        "data": {
          "title": "Notification Title",
          "message": "Notification Message Body"
        }
      }
    }
""".trimIndent().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/v1/projects/your-project-id/messages:send")
                    .header("Authorization", "Bearer $fcmServerKey")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle error
                        Log.w(TAG, "onFailure: ${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // Handle response
                        Log.d(TAG, "onResponse: ${response.body?.string()}")
                    }
                })
            }
        }


        private fun generateMessageId(): String {
            Log.d(TAG, "generateMessageId: ${Calendar.getInstance().time} ${userModel.name}")
            return "${Calendar.getInstance().time} ${userModel.name}"
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val message = remoteMessage.data["message"]
            Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
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