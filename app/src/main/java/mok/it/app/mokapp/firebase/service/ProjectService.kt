package mok.it.app.mokapp.firebase.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.utility.Utility.TAG

object ProjectService {
    fun getProjectsByIds(
        projectIds: List<String>,
        onComplete: (List<Project>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val projectsCollectionRef = Firebase.firestore.collection(Collections.projects)

        if (projectIds.isEmpty()) {
            return
        }
        projectsCollectionRef.whereIn(FieldPath.documentId(), projectIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val projectsList = mutableListOf<Project>()

                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    project?.let {
                        projectsList.add(it)
                    }
                }

                onComplete.invoke(projectsList)
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun getProjectData(projectId: String): LiveData<Project> {
        val project = MutableLiveData<Project>()
        Firebase.firestore.collection(Collections.projects).document(projectId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    project.value = document.toObject(Project::class.java)!!
                }
            }
        return project
    }

    fun addProject(project: HashMap<String, Any>) {
        Firebase.firestore.collection(Collections.projects)
            .add(project)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    fun updateProject(project: Project, editedBadge: HashMap<String, Any>) {
        Firebase.firestore.collection(Collections.projects).document(project.id)
            .update(editedBadge)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating document", e)
            }
    }

    fun getProjectsQuery() =
        Firebase.firestore.collection(Collections.projects)
            .orderBy("category", Query.Direction.ASCENDING)
            .orderBy("name", Query.Direction.ASCENDING)

}