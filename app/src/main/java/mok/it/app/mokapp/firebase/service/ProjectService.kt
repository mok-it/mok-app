package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.utility.Utility.TAG

object ProjectService {
    fun getProjectsQuery() =
        Firebase.firestore.collection(Collections.PROJECTS)
            .orderBy("category", Query.Direction.ASCENDING)
            .orderBy("name", Query.Direction.ASCENDING)

    fun getProjectsByIds(
        projectIds: List<String>
    ): LiveData<List<Project>> {
        val projectsLiveData = MutableLiveData<List<Project>>()

        // an empty list would crash the query
        if (projectIds.isEmpty()) {
            projectsLiveData.value = emptyList()
            return projectsLiveData
        }

        Firebase.firestore.collection(Collections.PROJECTS)
            .whereIn(FieldPath.documentId(), projectIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val projectsList = mutableListOf<Project>()

                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    project?.let {
                        projectsList.add(it)
                    }
                }

                projectsLiveData.value = projectsList

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        return projectsLiveData
    }

    fun getProjectData(projectId: String): LiveData<Project> {
        val project = MutableLiveData<Project>()
        Firebase.firestore.collection(Collections.PROJECTS).document(projectId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    project.value = document.toObject(Project::class.java)!!
                }
            }
        return project
    }

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
            "leaders" to project.leaders,
            "icon" to project.icon,
            "name" to project.name,
            "maxBadges" to project.maxBadges,
            "overall_progress" to project.overallProgress,
            "mandatory" to project.mandatory,
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
        val projectHashMap = hashMapOf(
            "name" to newProject.name,
            "description" to newProject.description,
            "category" to newProject.categoryEnum.toString(),
            "maxBadges" to newProject.maxBadges,
            "deadline" to newProject.deadline,
            "leaders" to newProject.leaders,
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

    fun getAllProjects(): LiveData<List<Project>> =
        Firebase.firestore.collection(Collections.PROJECTS)
            .orderBy("category")
            .orderBy("name")
            .snapshots()
            .map { s ->
                s.toObjects(Project::class.java)
            }
            .asLiveData()
}