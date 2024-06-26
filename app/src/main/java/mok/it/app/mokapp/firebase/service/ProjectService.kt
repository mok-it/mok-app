package mok.it.app.mokapp.firebase.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.utility.Utility.TAG

object ProjectService {

    fun getProjectsByIds(projectIds: List<String>): Flow<List<Project>> =
        Firebase.firestore.collection(Collections.PROJECTS)
            .whereIn(FieldPath.documentId(), projectIds)
            .snapshots()
            .map { s ->
                s.toObjects(Project::class.java)
            }
            .filterNotNull()

    fun getProjectData(projectId: String): Flow<Project> =
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId)
            .snapshots()
            .map { s ->
                s.toObject(Project::class.java)
            }
            .filterNotNull()

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

        Firebase.firestore.collection(Collections.PROJECTS)
            .add(projectHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
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
            .update(projectHashMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating document", e)
            }
    }

    fun getAllProjects(): Flow<List<Project>> =
        Firebase.firestore.collection(Collections.PROJECTS)
            .orderBy("category")
            .orderBy("name")
            .snapshots()
            .map { s ->
                s.toObjects(Project::class.java)
            }
            .filterNotNull()
}