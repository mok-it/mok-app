package mok.it.app.mokapp.firebase

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG


object FirebaseUserObject {
    lateinit var userModel: User
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var userModelFlow: Flow<User>

    /**
     * Refreshes the currentUser and userModel objects and invokes the given method on success, if there's one
     * @param context the context of the activity (required for the Toasts)
     * @param onSuccessFunction the function to be invoked on success
     */
    fun refreshCurrentUserAndUserModel(
        context: Context,
        onSuccessFunction: (() -> Unit)? = null,
    ) {
        refreshCurrentUserAndUserModelRecursive(context, onSuccessFunction, 1)
        userModelFlow = Firebase.firestore.collection(Collections.USERS)
            .document(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("FirebaseAuth user is null")
            )
            .snapshots()
            .map { s ->
                s.toObject(User::class.java)
                    ?: throw NullPointerException("User object is null")
            }
    }

    private fun refreshCurrentUserAndUserModelRecursive(
        context: Context,
        onSuccessFunction: (() -> Unit)? = null,
        numberOfConsecutiveCalls: Int
    ) {
        val numberOfMaxTries = 100
        Log.d(TAG, FirebaseAuth.getInstance().currentUser?.uid.toString())
        Firebase.firestore.collection(Collections.USERS)
            .document(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("FirebaseAuth user is null")
            )
            .get()
            .addOnSuccessListener { document ->
                val userToBe = document.toObject(User::class.java)
                Log.d(TAG, "refreshCurrentUser(): got document ${userToBe.toString()}")
                if (userToBe != null) {
                    userModel = userToBe
                    currentUser = FirebaseAuth.getInstance().currentUser
                        ?: throw Exception("FirebaseAuth user is null")
                    Log.d(TAG, "refreshCurrentUser(): user refreshed")
                    onSuccessFunction?.invoke()
                } else if (numberOfConsecutiveCalls <= numberOfMaxTries) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(
                            context,
                            "Kis türelmet...",
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshCurrentUserAndUserModelRecursive(
                            context,
                            onSuccessFunction,
                            numberOfConsecutiveCalls + 1
                        )
                    }, 1000)
                }
            }
    }
}