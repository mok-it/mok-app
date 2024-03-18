package mok.it.app.mokapp.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project

object ProjectService : IProjectService {
    override fun getProjectsByIds(
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

    fun getAllProjects(): LiveData<List<Project>> {
        val projects: MutableLiveData<List<Project>> = MutableLiveData()
        val projectsCollectionRef = Firebase.firestore.collection(Collections.PROJECTS)

        projectsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->

                for (document in querySnapshot.documents) {
                    val project = document.toObject(Project::class.java)
                    if (project != null) {
                        projects.value = projects.value?.plus(project) ?: listOf(project)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error getting projects", e)
            }
        return projects
    }
}