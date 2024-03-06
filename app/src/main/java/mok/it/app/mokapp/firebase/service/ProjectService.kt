package mok.it.app.mokapp.firebase.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project

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

    fun getProjectData(projectId: String, onComplete: (Project) -> Unit = {}): LiveData<Project> {
        val project = MutableLiveData<Project>()
        Firebase.firestore.collection(Collections.projects).document(projectId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    project.value = document.toObject(Project::class.java)!!
                    onComplete.invoke(project.value!!)
                }
            }
        return project
    }
}