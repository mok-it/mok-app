package mok.it.app.mokapp.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.map
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project

object ProjectService {
    fun getProjectsByIds(
        projectIds: List<String>,
        onComplete: (List<Project>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val projectsCollectionRef = Firebase.firestore.collection(Collections.PROJECTS)

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

    fun getAllProjects(): LiveData<List<Project>> =
        com.google.firebase.Firebase.firestore.collection(Collections.PROJECTS)
            .orderBy("category")
            .orderBy("name")
            .snapshots()
            .map { s ->
                s.toObjects(Project::class.java)
            }
            .asLiveData()
}