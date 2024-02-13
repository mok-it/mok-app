package mok.it.app.mokapp.service

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Project

object ProjectService : IProjectService{
    override fun getProjectsByIds(
        projectIds: List<String>,
        onComplete: (List<Project>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val projectsCollectionRef = Firebase.firestore.collection(Collections.projects)

        if (projectIds.isEmpty()){
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
}