package mok.it.app.mokapp.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import mok.it.app.mokapp.activity.MainActivity.Companion.TAG
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import kotlin.math.min

object UserService : IUserService {
    const val userDocNotFound = "User document not found"
    override fun addBadges(
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

    override fun getBadgeAmountSum(
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

    override fun getProjectBadges(
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

    override fun getProjectUsersAndBadges(
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

    override fun joinUsersToProject(
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

    override fun removeUserFromProject(
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

    data class BadgeData(
        var finishedProjectCount: Int,
        var finishedProjectBadgeSum: Int,
        var category: Category
    )

    override fun getBadgeSumForUserInEachCategory(
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
}