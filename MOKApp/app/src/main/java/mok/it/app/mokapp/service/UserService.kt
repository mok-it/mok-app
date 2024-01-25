package mok.it.app.mokapp.service

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import java.util.concurrent.CountDownLatch

object UserService : IUserService {
    override fun addBadges(
        userId: String,
        badgeId: String,
        badgeAmount: Int,
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocumentRef = Firebase.firestore.collection(Collections.users)
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
                onFailure.invoke(Exception("User document not found"))
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
        val userDocumentRef = Firebase.firestore.collection(Collections.users)
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
                onFailure.invoke(Exception("User document not found"))
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
        val userDocumentRef = Firebase.firestore.collection(Collections.users)
            .document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val projectBadges =
                    documentSnapshot.data?.get("projectBadges") as? Map<String, Int> ?: mapOf()

                onComplete.invoke(projectBadges)
            } else {
                onFailure.invoke(Exception("User document not found"))
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
        val usersCollectionRef = Firebase.firestore.collection(Collections.users)

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
            .collection(Collections.badges).document(projectId)

        for (userId in userIds) {
            val userDocumentRef = Firebase.firestore.collection(Collections.users).document(userId)
            batch.update(userDocumentRef, "joinedBadges", FieldValue.arrayUnion(projectId))
            batch.update(projectDocumentRef, "members", FieldValue.arrayUnion(userId))
        }

        batch.commit()
            .addOnSuccessListener {
                onComplete.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    override fun getBadgeSumForUserInCategory(
        userId: String,
        category: String,
        onComplete: (Int) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val projectsList = mutableListOf<Project>()

        val userDocumentRef = Firebase.firestore.collection(Collections.users)
            .document(userId)

        val projectsCollectionRef = Firebase.firestore.collection(Collections.badges)
        projectsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    if (project != null){
                        projectsList.add(project)
                    }
                }
                userDocumentRef.get()
                    .addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            val userDocument = userTask.result

                            if (userDocument != null && userDocument.exists()) {
                                val projectBadges = userDocument.data?.get("projectBadges") as? Map<String, Int>

                                if (projectBadges != null) {
                                    val projectsInCategory = projectBadges
                                        .filterKeys { projectId ->
                                            val projectInList = projectsList.find { it.id == projectId }
                                            projectInList?.category == category
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


    private fun isProjectInCategory(projectId: String, category: String, onComplete: (Boolean) -> Unit) {
        val projectDocumentRef = Firebase.firestore.collection(Collections.badges)
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