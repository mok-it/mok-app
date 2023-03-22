package mok.it.app.mokapp.fragments.viewmodels

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.User

class MemberViewModel : ViewModel() {
    fun getUserBadgeCountByCategory(user: User, category: Category): LiveData<Int> {
        val count = MutableLiveData(0)

        user.collectedBadges.chunked(10).let {
            it.forEach { batch ->
                Firebase.firestore.collection("projects")
                    .whereEqualTo("category", category.toString())
                    .whereIn(FieldPath.documentId(), batch)
                    .get()
                    .addOnSuccessListener { documents ->
                        count.value = count.value?.plus(documents.size())
                    }
            }
        }

        return count
    }

    fun loadImage(imageView: ImageView, imageURL: String): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}