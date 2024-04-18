package mok.it.app.mokapp.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import kotlin.math.min

object UserService {
    const val userDocNotFound = "User document not found"

    fun getUserById(
        userId: String
    ): LiveData<User> {
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)
        val userLiveData = MutableLiveData<User>()

        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    user.apply {
                        userLiveData.value = this
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserService", "Error getting user", e)
            }
        return userLiveData
    }

    fun getUsersById(userIds: List<String>): Flow<List<User>> =
        Firebase.firestore.collection(Collections.USERS)
            .whereIn(FieldPath.documentId(), userIds)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }

    @JvmName("getUsersByIdFromUserList")
    fun getUsersById(users: List<User>) =
        getUsersById(users.map { it.documentId })


    fun addBadges(
        userId: String,
        badgeId: String,
        badgeAmount: Int,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentBadges =
                    documentSnapshot.data?.get("projectBadges") as? HashMap<String, Int>
                        ?: hashMapOf()
                currentBadges[badgeId] = badgeAmount

                userDocumentRef.update("projectBadges", currentBadges)
                    .addOnSuccessListener {
                        onComplete.invoke()
                    }
                    .addOnFailureListener { e ->
                        onFailure.invoke(e)
                    }
            } else {
                onFailure.invoke(Exception(userDocNotFound))
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getBadgeAmountSum(
        userId: String,
        onComplete: (Int) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentBadges =
                    documentSnapshot.data?.get("projectBadges") as? HashMap<String, Int>
                        ?: hashMapOf()

                // Calculate the sum of badge amounts
                val sum = currentBadges.values.sum()

                onComplete.invoke(sum)
            } else {
                onFailure.invoke(Exception(userDocNotFound))
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getAllUsers(): LiveData<List<User>> =
        Firebase.firestore.collection(Collections.USERS)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }.asLiveData()

    fun getProjectBadges(
        userId: String,
        onComplete: (Map<String, Int>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val projectBadges =
                    documentSnapshot.data?.get("projectBadges") as? Map<String, Int> ?: mapOf()

                onComplete.invoke(projectBadges)
            } else {
                onFailure.invoke(Exception(userDocNotFound))
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getProjectUsersAndBadges(
        projectId: String,
        onComplete: (Map<String, Int>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val usersCollectionRef = Firebase.firestore.collection(Collections.USERS)

        usersCollectionRef.whereGreaterThan("projectBadges.$projectId", 0)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val result = mutableMapOf<String, Int>()

                for (document in querySnapshot.documents) {
                    val projectBadges = document["projectBadges"] as? Map<String, Long>

                    // Find the badgeAmount for the specified projectId
                    val badgeAmount = projectBadges?.get(projectId)?.toInt()

                    // Check if badgeAmount is greater than 0
                    if (badgeAmount != null && badgeAmount > 0) {
                        result[document.id] = badgeAmount
                    }
                }

                onComplete.invoke(result)
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun joinUsersToProject(
        projectId: String,
        userIds: List<String>,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = Firebase.firestore.batch()
        val projectDocumentRef = Firebase.firestore
            .collection(Collections.PROJECTS).document(projectId)

        for (userId in userIds) {
            val userDocumentRef = Firebase.firestore.collection(Collections.USERS).document(userId)
            batch.update(userDocumentRef, "joinedBadges", FieldValue.arrayUnion(projectId))
            batch.update(projectDocumentRef, "members", FieldValue.arrayUnion(userId))
            batch.update(userDocumentRef, "projectBadges.$projectId", 0)
        }

        batch.commit()
            .addOnSuccessListener {
                onComplete.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun removeUserFromProject(
        projectId: String,
        userId: String,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = Firebase.firestore.batch()
        val projectDocumentRef = Firebase.firestore
            .collection(Collections.PROJECTS).document(projectId)

        val userDocumentRef = Firebase.firestore.collection(Collections.USERS).document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                batch.update(userDocumentRef, "projectBadges.$projectId", FieldValue.delete())
                Log.d("UserService", "Project badges remove from user")

                batch.update(userDocumentRef, "joinedBadges", FieldValue.arrayRemove(projectId))
                batch.update(projectDocumentRef, "members", FieldValue.arrayRemove(userId))

                batch.commit()
                    .addOnSuccessListener {
                        onComplete.invoke()
                    }
                    .addOnFailureListener { e ->
                        onFailure.invoke(e)
                    }
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getBadgeSumForUserInCategory(
        userId: String,
        category: String,
        onComplete: (Int) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val projectsList = mutableListOf<Project>()

        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
            .document(userId)

        val projectsCollectionRef = Firebase.firestore.collection(Collections.PROJECTS)
        projectsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    if (project != null) {
                        projectsList.add(project)
                    }
                }
                userDocumentRef.get()
                    .addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            val userDocument = userTask.result

                            if (userDocument != null && userDocument.exists()) {
                                val projectBadges =
                                    userDocument.data?.get("projectBadges") as? Map<String, Int>

                                if (projectBadges != null) {
                                    val projectsInCategory = projectBadges
                                        .filterKeys { projectId ->
                                            val projectInList =
                                                projectsList.find { it.id == projectId }
                                            projectInList?.categoryEnum?.name == category
                                        }
                                    val sum = projectsInCategory.values.sum()
                                    onComplete.invoke(sum)
                                } else {
                                    onFailure.invoke(Exception("User document does not contain projectBadges"))
                                }
                            } else {
                                onFailure.invoke(Exception("User document not found"))
                            }
                        } else {
                            onFailure.invoke(userTask.exception ?: Exception("Unknown error"))
                        }
                    }

            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun capProjectBadges(projectId: String) {
        val db = Firebase.firestore

        db.collection(Collections.PROJECTS).document(projectId).get()
            .addOnSuccessListener { projectSnapshot ->
                val project = projectSnapshot.toObject(Project::class.java)

                project?.members?.forEach { userId ->
                    db.collection(Collections.USERS).document(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(User::class.java)

                            user?.projectBadges?.get(projectId)?.let { projectBadgeValue ->
                                user.projectBadges[projectId] =
                                    min(projectBadgeValue, project.maxBadges)
                                db.collection(Collections.USERS).document(userId).set(user)
                            }
                        }
                }
            }
    }

    private fun isProjectInCategory(
        projectId: String,
        category: String,
        onComplete: (Boolean) -> Unit
    ) {
        val projectDocumentRef = Firebase.firestore.collection(Collections.PROJECTS)
            .document(projectId)

        projectDocumentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val projectSnapshot = task.result

                    if (projectSnapshot != null && projectSnapshot.exists()) {
                        val projectCategory = projectSnapshot.getString("category")
                        onComplete.invoke(projectCategory == category)
                    } else {
                        onComplete.invoke(false)
                    }
                } else {
                    onComplete.invoke(false)
                }
            }
    }
}
