package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.utility.Utility.TAG

object CommentService {
    fun getCommentsQuery(projectId: String) =
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
            .collection(Collections.COMMENTS)
            .orderBy("time", Query.Direction.DESCENDING)

    fun addComment(projectId: String, comment: Comment) {
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
            .collection(Collections.COMMENTS)
            .add(comment).addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }

    }
}