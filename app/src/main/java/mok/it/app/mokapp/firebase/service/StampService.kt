package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.utility.Utility.TAG

object StampService {
    private const val USERDOCNOTFOUND = "User document not found"

    fun addStamp(
        userId: String,
        stampId: String,
        stampAmount: Int,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit,
    ){
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()){
                    val currentStamps = documentSnapshot.data?.get("stamps") as? HashMap<String, Int>
                        ?: hashMapOf()

                    currentStamps[stampId] = (currentStamps[stampId] ?: 0) + stampAmount;

                    userDocumentRef.update("stamps", currentStamps)
                        .addOnSuccessListener {
                            onComplete.invoke()
                        }
                        .addOnFailureListener { e ->
                            onFailure.invoke(e)
                        }
                } else {
                    onFailure.invoke( Exception(USERDOCNOTFOUND) )
                }
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getStamps(
        userId: String
    ) : LiveData<MutableMap<String, Int>>{
        val stamps = MutableLiveData<MutableMap<String,Int>>()

        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                stamps.value = documentSnapshot.data?.get("stamps") as? HashMap<String,Int> ?: hashMapOf()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, e.message.toString())
            }

        return stamps
    }
}