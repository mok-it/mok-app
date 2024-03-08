package mok.it.app.mokapp.fragments.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.fragments.DetailsFragment
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User

class DetailsFragmentViewModel : ViewModel() {
    val members: LiveData<Array<User>> get() = _members
    private val _members: MutableLiveData<Array<User>> by lazy {
        MutableLiveData<Array<User>>()
    }

    init {
        _members.value = arrayOf()
    }

    fun setMembers(users: Array<User>) {
        _members.value = users
    }

    private fun getMemberIds(projectId: String) {
        val docRef = Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    Log.d(DetailsFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    val model = document.toObject(Project::class.java)!!
                    getMembers(model.members)
                    Log.d(DetailsFragment.TAG, "Model data: $model")
                } else {
                    Log.d(DetailsFragment.TAG, "No such document or data is null")
                }
            }
    }

    fun getMembers(memberIds: List<String>?) {
        _members.value = arrayOf()
        memberIds?.forEach {
            val docRef = Firebase.firestore.collection(Collections.USERS).document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(DetailsFragment.TAG, "fetched document: ${document.id}")
                        val user =
                            document.toObject(User::class.java)
                        if (user != null) {
                            _members.value = _members.value?.plus(user)
                        }
                    }
                }
        }
    }

    fun completed(userId: String, project: Project) {
        Log.d(DetailsFragment.TAG, "badge completed with id ${project.name}")

        val userRef = Firebase.firestore.collection(Collections.USERS).document(userId)
        userRef.update("joinedBadges", FieldValue.arrayRemove(project.id))
            .addOnSuccessListener {
                Log.d(DetailsFragment.TAG, project.name + " removed from " + userId)
            }.addOnFailureListener { e -> Log.d(DetailsFragment.TAG, e.message.toString()) }

        userRef.update("collectedBadges", FieldValue.arrayUnion(project.id))

        Firebase.firestore.collection(Collections.PROJECTS).document(project.id)
            .update("members", FieldValue.arrayRemove(userId))
            .addOnCompleteListener {
                getMemberIds(project.id)
                Log.d(DetailsFragment.TAG, "member removed from badge's collection")
            }

        MyFirebaseMessagingService.sendNotificationToUsersById(
            "Projekt teljesítve!",
            "A(z) \"${project.name}\" nevű projektet sikeresen teljesítetted, kapott mancsaid száma:",//TODO
            listOf(userId)
        )
    }
}