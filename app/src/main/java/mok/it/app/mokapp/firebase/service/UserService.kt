package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.model.enums.Category
import mok.it.app.mokapp.utility.Utility.TAG
import kotlin.math.min

object UserService {
    private const val USERDOCNOTFOUND = "User document not found"
    fun addBadges(
        userId: String,
        badgeId: String,
        badgeAmount: Int,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit,
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
        onFailure: (Exception) -> Unit,
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
        onFailure: (Exception) -> Unit,
    ) {
        val userDocumentRef = Firebase.firestore.collection(Collections.USERS)
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
        onFailure: (Exception) -> Unit,
    ) {
        val batch = Firebase.firestore.batch()
        val projectDocumentRef = Firebase.firestore
            .collection(Collections.PROJECTS).document(projectId)

        for (userId in userIds) {
            val userDocumentRef = Firebase.firestore.collection(Collections.USERS).document(userId)
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
        onFailure: (Exception) -> Unit,
    ) {
        val batch = Firebase.firestore.batch()
        val projectDocumentRef = Firebase.firestore
            .collection(Collections.PROJECTS).document(projectId)

        val userDocumentRef = Firebase.firestore.collection(Collections.USERS).document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                batch.update(userDocumentRef, "projectBadges.$projectId", FieldValue.delete())
                Log.d(TAG, "Project badges remove from user")

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

    data class BadgeData(
        var finishedProjectCount: Int,
        var finishedProjectBadgeSum: Int,
        var category: Category,
    )

    fun getBadgeSumForUserInEachCategory(
        userId: String,
    ): LiveData<List<BadgeData>> {
        val badgesLiveData: MutableLiveData<List<BadgeData>> = MutableLiveData()
        Firebase.firestore.collection(Collections.PROJECTS).get()
            .addOnSuccessListener { querySnapshot ->
                val projects =
                    querySnapshot.documents.mapNotNull { it.toObject(Project::class.java) }
                val categories = projects.map { it.categoryEnum }.distinct()

                Firebase.firestore.collection(Collections.USERS).document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val projectBadges =
                                documentSnapshot.data?.get("projectBadges") as? Map<String, Long>
                                    ?: mapOf()

                            val badgeDataList = categories.map { category ->
                                val projectsInCategory =
                                    projects.filter { it.categoryEnum == category }

                                val finishedProjectCount =
                                    projectsInCategory.count { it.id in projectBadges.keys }
                                val finishedProjectBadgeSum =
                                    projectsInCategory.sumOf { projectBadges[it.id] ?: 0 }.toInt()

                                BadgeData(finishedProjectCount, finishedProjectBadgeSum, category)
                            }

                            badgesLiveData.value = badgeDataList
                        }
                    }
            }

        return badgesLiveData
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
        onComplete: (Boolean) -> Unit,
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

    fun getMembersForProject(projectId: String): LiveData<List<User>> {
        val members = MutableLiveData<List<User>>() // Initialize with an empty list
        val docRef = Firebase.firestore.collection(Collections.PROJECTS).document(projectId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                val model = document.toObject(Project::class.java)!!

                // Use Coroutines for cleaner asynchronous task management
                members.value = kotlinx.coroutines.runBlocking {
                    model.members.map { memberId ->
                        async {
                            val memberDocRef = Firebase.firestore
                                .collection(Collections.USERS).document(memberId)
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
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
            .collection(Collections.COMMENTS)
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

    fun getUser(userId: String): Flow<User> =
        Firebase.firestore.collection(Collections.USERS).document(userId)
            .snapshots()
            .map { s ->
                s.toObject(User::class.java)
            }
            .filterNotNull()


    fun getAllUsers(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()
        Firebase.firestore.collection(Collections.USERS).orderBy("name", Query.Direction.ASCENDING)
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

    fun updateFcmTokenIfEmptyOrOutdated() {
        if (FirebaseUserObject.currentUser == null) {
            Log.e(TAG, "updateFcmTokenIfEmptyOrOutdated: currentUser is null")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM token: $token")

                if (token != FirebaseUserObject.userModel.fcmToken
                ) {
                    updateFcmToken(token)
                }
            } else {
                Log.w(
                    TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
            }
        }
    }

    fun updateFcmToken(token: String) {
        Firebase.firestore.collection(Collections.USERS)
            .document(FirebaseUserObject.userModel.documentId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(TAG, "onNewToken: token uploaded to firestore, new token: $token")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "onNewToken: token upload failed", exception)
            }
    }

    fun getUsersQuery(): Query =
        Firebase.firestore.collection(Collections.USERS)
            .orderBy("name", Query.Direction.ASCENDING)

}
