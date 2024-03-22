package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Collections.users
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.TAG
import kotlin.math.min

object UserService {
    private const val USERDOCNOTFOUND = "User document not found"
    fun addBadges(
        userId: String,
        badgeId: String,
        badgeAmount: Int,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(users)
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
                onFailure.invoke(Exception(USERDOCNOTFOUND))
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
        val userDocumentRef = Firebase.firestore.collection(users)
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
                onFailure.invoke(Exception(USERDOCNOTFOUND))
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getProjectBadges(
        userId: String,
        onComplete: (Map<String, Int>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(users)
            .document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val projectBadges =
                    documentSnapshot.data?.get("projectBadges") as? Map<String, Int> ?: mapOf()

                onComplete.invoke(projectBadges)
            } else {
                onFailure.invoke(Exception(USERDOCNOTFOUND))
            }
        }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun getProjectUsersAndBadges(
        projectId: String,
    ): LiveData<MutableMap<String, Int>> {
        val usersAndBadges = MutableLiveData<MutableMap<String, Int>>()
        val usersCollectionRef = Firebase.firestore.collection(users)

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
                usersAndBadges.value = result
            }
            .addOnFailureListener { e ->
                Log.d(TAG, e.message.toString())
            }
        return usersAndBadges
    }

    fun addUsersToProject(
        projectId: String,
        userIds: List<String>,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = Firebase.firestore.batch()
        val projectDocumentRef = Firebase.firestore
            .collection(Collections.projects).document(projectId)

        for (userId in userIds) {
            val userDocumentRef = Firebase.firestore.collection(users).document(userId)
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
            .collection(Collections.projects).document(projectId)

        val userDocumentRef = Firebase.firestore.collection(users).document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                batch.update(userDocumentRef, "projectBadges.$projectId", FieldValue.delete())
                Log.d(TAG, "Project badges remove from user")

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

        val userDocumentRef = Firebase.firestore.collection(users)
            .document(userId)

        val projectsCollectionRef = Firebase.firestore.collection(Collections.projects)
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

        db.collection(Collections.projects).document(projectId).get()
            .addOnSuccessListener { projectSnapshot ->
                val project = projectSnapshot.toObject(Project::class.java)

                project?.members?.forEach { userId ->
                    db.collection(users).document(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(User::class.java)

                            user?.projectBadges?.get(projectId)?.let { projectBadgeValue ->
                                user.projectBadges[projectId] =
                                    min(projectBadgeValue, project.maxBadges)
                                db.collection(users).document(userId).set(user)
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
        val projectDocumentRef = Firebase.firestore.collection(Collections.projects)
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

    fun getMembersForProject(projectId: String): LiveData<List<User>> {
        val members = MutableLiveData<List<User>>() // Initialize with an empty list
        val docRef = Firebase.firestore.collection(Collections.projects).document(projectId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                val model = document.toObject(Project::class.java)!!

                // Use Coroutines for cleaner asynchronous task management
                members.value = kotlinx.coroutines.runBlocking {
                    model.members.map { memberId ->
                        async {
                            val memberDocRef = Firebase.firestore
                                .collection(users).document(memberId)
                            memberDocRef.get().await().toObject(User::class.java)
                        }
                    }.awaitAll().filterNotNull() // Filter out any failed fetches
                }
            } else {
                Log.d(TAG, "No such document or data is null")
                members.value = emptyList() // Explicitly set empty state
            }
        }
        return members
    }

    fun getMostRecentComment(projectId: String): LiveData<Comment> {
        val mostRecentComment = MutableLiveData<Comment>()
        Firebase.firestore.collection(Collections.projects).document(projectId)
            .collection(Collections.commentsRelativePath)
            .orderBy("time", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { collection ->
                if (collection != null && collection.documents.isNotEmpty()) {
                    mostRecentComment.value =
                        collection.documents[0].toObject(Comment::class.java)!!
                }
            }
        return mostRecentComment
    }

    /**
     * Mark a project as completed for a user
     * 1. Remove the project from the user's joinedBadges
     * 2. Add the project to the user's collectedBadges
     * 3. Remove the user from the project's members
     * @param project: the project to be marked as completed
     * @param userId: the user who completed the project
     */
    fun markProjectAsCompletedForUser(project: Project, userId: String) {
        val userRef = Firebase.firestore.collection(users).document(userId)
        userRef.update("joinedBadges", FieldValue.arrayRemove(project.id))
            .addOnSuccessListener {
                userRef.update("collectedBadges", FieldValue.arrayUnion(project.id))
                    .addOnSuccessListener {
                        Firebase.firestore.collection(Collections.projects).document(project.id)
                            .update("members", FieldValue.arrayRemove(userId))
                            .addOnCompleteListener {
                                Log.d(TAG, "member removed from badge's collection")
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, e.message.toString())
                            }
                    }
            }.addOnFailureListener { e -> Log.d(TAG, e.message.toString()) }
    }

    fun getUser(userId: String): LiveData<User> {
        val user = MutableLiveData<User>()
        Firebase.firestore.collection(users).document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    user.value = document.toObject(User::class.java)
                }
            }
        return user
    }

    fun getAllUsers(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()
        Firebase.firestore.collection(users).orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = mutableListOf<User>()
                for (document in querySnapshot.documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }
                usersLiveData.value = users
            }
        return usersLiveData
    }

    fun getParticipantsQuery(members: List<String>): Query =
        Firebase.firestore.collection(users)
            .orderBy("name", Query.Direction.ASCENDING)
            .whereIn(
                FieldPath.documentId(),
                members
            ) //NOTE: can not handle lists of size greater than 30

    fun getUsersQuery(): Query =
        Firebase.firestore.collection(users)
            .orderBy("name", Query.Direction.ASCENDING)

}
