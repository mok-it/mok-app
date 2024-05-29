package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.utility.Utility.TAG

object CommentService {
    fun addComment(projectId: String, comment: Comment) {
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
                .collection(Collections.COMMENTS)
                .add(comment).addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }
    }

    fun getComments(projectId: String): LiveData<List<Comment>> =
            Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
                    .collection(Collections.COMMENTS)
                    .orderBy("time")
                    .snapshots()
                    .map { s ->
                        s.toObjects(Comment::class.java)
                    }
                    .asLiveData()
}