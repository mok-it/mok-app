package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
    fun setProjectBadgesOfUser(
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

    fun setProjectBadgesOfMultipleUsers(
        userIdToBadgeValueMap: Map<String, Int>,
        projectId: String,
    ) {
        val db = Firebase.firestore
        val batch = db.batch()

        userIdToBadgeValueMap.forEach { (userId, badgeValue) ->
            val userDocumentRef = db.collection(Collections.USERS).document(userId)
            batch.update(userDocumentRef, "projectBadges.$projectId", badgeValue)
        }

        batch.commit()
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

    /**
     * Returns a Flow containing a map of userIds and the amount of badges they collected on the project with the specified projectId.
     * */
    fun getProjectUsersAndBadges(projectId: String): Flow<Map<String, Int>> =
        Firebase.firestore.collection(Collections.USERS)
            .whereGreaterThanOrEqualTo("projectBadges.$projectId", 0)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    val projectBadges = document["projectBadges"] as? Map<String, Long>
                    Log.d(TAG, "projectBadges: $projectBadges of user: ${document["name"]}")
                    val badgeAmount = projectBadges?.get(projectId)?.toInt()
                    if (badgeAmount != null) {
                        document.id to badgeAmount
                    } else {
                        null
                    }
                }.toMap()
            }
            .filterNotNull()

    fun addUsersToProject(
        projectId: String,
        userIds: List<String>,
        onComplete: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
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

    fun getUsers(): Flow<List<User>> {
        return Firebase.firestore.collection(Collections.USERS)
            .orderBy("name", Query.Direction.ASCENDING)
            .snapshots()
            .map { s -> s.toObjects(User::class.java) }
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

    @OptIn(DelicateCoroutinesApi::class)
    fun updateFcmToken(token: String) {
        GlobalScope.launch {
            val userModel = FirebaseUserObject.userModelFlow.firstOrNull()
            if (userModel == null) {
                Log.e(TAG, "userModelFlow is not available yet.")
                return@launch
            }

            Firebase.firestore.collection(Collections.USERS)
                .document(userModel.documentId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d(TAG, "onNewToken: token uploaded to firestore, new token: $token")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "onNewToken: token upload failed", exception)
                }
        }
    }

    fun getUsers(userIds: List<String>): Flow<List<User>> {
        val ids = userIds.ifEmpty { listOf("_") }
        return Firebase.firestore.collection(Collections.USERS)
            .whereIn(FieldPath.documentId(), ids)
            .snapshots()
            .map { s -> s.toObjects(User::class.java) }
    }
}
