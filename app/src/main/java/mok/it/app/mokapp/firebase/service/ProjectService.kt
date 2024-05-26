package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.utility.Utility.TAG

object ProjectService {

    fun getProjectsByIds(
        projectIds: List<String>,
    ): LiveData<List<Project>> {
        val projectsLiveData = MutableLiveData<List<Project>>()

        // an empty list would crash the query
        if (projectIds.isEmpty()) {
            projectsLiveData.value = emptyList()
            return projectsLiveData
        }

        Firebase.firestore.collection(Collections.PROJECTS)
            .whereIn(FieldPath.documentId(), projectIds).get()
            .addOnSuccessListener { querySnapshot ->
                val projectsList = mutableListOf<Project>()

                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    project?.let {
                        projectsList.add(it)
                    }
                }

                projectsLiveData.value = projectsList

            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        return projectsLiveData
    }

    fun getProject(projectId: String): Flow<Project> =
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId).snapshots()
            .map { s ->
                s.toObject(Project::class.java)
            }.filterNotNull()


    fun addProject(project: Project) {

        Log.d(
            TAG,
            "Created a new Project called ${project.name} with category ${project.categoryEnum}"
        )

        val projectHashMap = hashMapOf(
            "category" to project.categoryEnum.toString(),
            "created" to project.created,
            "creator" to project.creator,
            "deadline" to project.deadline,
            "description" to project.description,
            "projectLeader" to project.projectLeader,
            "icon" to project.icon,
            "name" to project.name,
            "maxBadges" to project.maxBadges,
            "overall_progress" to project.overallProgress,
        )

        Firebase.firestore.collection(Collections.PROJECTS).add(projectHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    /**
     * Updates the project with the given ID with the new project data. Caution: only updates certain fields.
     */
    fun updateProject(oldProjectId: String, newProject: Project) {
        val projectHashMap: HashMap<String, Any> = hashMapOf(
            "name" to newProject.name,
            "description" to newProject.description,
            "category" to newProject.categoryEnum.toString(),
            "maxBadges" to newProject.maxBadges,
            "deadline" to newProject.deadline,
            "projectLeader" to newProject.projectLeader,
        )

        Firebase.firestore.collection(Collections.PROJECTS).document(oldProjectId)
            .update(projectHashMap).addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error updating document", e)
            }
    }

    fun getAllProjects(): LiveData<List<Project>> =
        Firebase.firestore.collection(Collections.PROJECTS).orderBy("category").orderBy("name")
            .snapshots().map { s ->
                s.toObjects(Project::class.java)
            }.asLiveData()

    suspend fun setMembersOfProject(projectId: String, members: List<String>) {
        val originalMembers =
            (Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
                .get().await()["members"] as List<String>).toSet()

        val newlyAddedUsers =
            members - originalMembers

        //add the project to the newly added users' projectBadges map with 0
        newlyAddedUsers.forEach { userId ->
            Firebase.firestore.collection(Collections.USERS).document(userId)
                .update("projectBadges.$projectId", 0)
        }

        val newlyRemovedUsers = originalMembers - members.toSet()

        //remove the project from the newly removed users' projectBadges map
        newlyRemovedUsers.forEach { userId ->
            Firebase.firestore.collection(Collections.USERS).document(userId)
                .update("projectBadges.$projectId", null)
        }

        // update the project with the new member list
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
            .update("members", members)
    }
}