package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import io.reactivex.rxjava3.internal.util.NotificationLite.isComplete
import kotlinx.android.synthetic.main.fragment_create_badge.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User
import java.util.*
import kotlin.collections.ArrayList

class EditBadgeFragment(private val badge: Project, private val detailsFragment: EditBadgeListener) : CreateBadgeFragment(badge.category) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            badge_name.setText(badge.name)
            badge_description.setText(badge.description)
            val cal: Calendar = Calendar.getInstance()
            cal.time = badge.deadline
            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            textViewTitle.text = getString(R.string.edit_badge_text)
            create_button.text = getString(R.string.edit_text)
        }
        selectedEditors = badge.editors as ArrayList<String>
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getUsers(){
        users = ArrayList()
        firestore.collection(userCollectionPath)
            .whereArrayContains("categories", category)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        users.add(document.toObject(User::class.java))
                    }
                    names = Array(users.size){i->users[i].name}
                    checkedNames = BooleanArray(users.size){ i ->
                        badge.editors.contains(users[i].documentId)
                    }
                    super.initEditorsDialog()
                }
            }
    }

    override fun onCreateBadgePressed() {
        val shouldCloseDialog = onEditBadge()
        if (shouldCloseDialog) {
            closeDialog()
            detailsFragment.onEdited()
        }
    }

    private fun onEditBadge(): Boolean {
        val success = commitEditedBadgeToDatabase()
        return if (success) {
            true
        } else {
            toast(R.string.error_occurred)
            false
        }
    }

    private fun commitEditedBadgeToDatabase(): Boolean{
        val deadline = Date(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
        val editedBadge = hashMapOf(
            "category" to category,
            "created" to Date(),
            "creator" to ContainerActivity.userModel.documentId,
            "deadline" to deadline,
            "description" to descriptionTIET.text.toString(),
            "editors" to selectedEditors,
            "icon" to "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef",
            "name" to nameTIET.text.toString(),
            "overall_progress" to 0,
            "mandatory" to false

        )
        firestore.collection("/projects")
            .document(badge.id)
            .update(editedBadge as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot edited with ID: ${badge.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error editing document", e)
            }

        return true
    }

    interface EditBadgeListener{
        fun onEdited()
    }
}