package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_create_badge.datePicker
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import java.util.Calendar
import java.util.Date

@Suppress("DEPRECATION")
class EditProjectFragment : CreateProjectFragment() {

    private val args: EditProjectFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.badgeName.setText(args.badge.name)
        binding.badgeDescription.setText(args.badge.description)
        Log.d(TAG, "category dropdown count: ${binding.badgeMcs.adapter.count}")
        binding.badgeMcs.setText(
            args.badge.categoryEnum.toString(),
            false
        ) // 0 nem csinál semmit, 1<= kifagy
        //binding.badgeMcs.setSelection(Category.values().indexOf(args.badge.categoryEnum))
        val cal: Calendar = Calendar.getInstance()
        cal.time = args.badge.deadline
        datePicker.updateDate(
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        )
        binding.textViewTitle.text = getString(R.string.edit_project)
        binding.createButton.text = getString(R.string.edit_text)

        selectedEditors = args.badge.editors.toMutableList()
    }

    override fun getUsers() {
        users = ArrayList()
        firestore.collection(userCollectionPath)
            .whereArrayContains("categories", args.badge.categoryEnum)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        users.add(document.toObject(User::class.java))
                    }
                    names = Array(users.size) { i -> users[i].name }
                    checkedNames = BooleanArray(users.size) { i ->
                        args.badge.editors.contains(users[i].documentId)
                    }
                    super.initEditorsDialog()
                }
            }
    }

    override fun onCreateBadgePressed() {
        val shouldCloseDialog = onEditBadge()
        if (shouldCloseDialog) {
            findNavController().navigateUp()
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

    private fun commitEditedBadgeToDatabase(): Boolean {
        val deadline = Date(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
        val editedBadge = hashMapOf(
            "category" to binding.badgeMcs.text.toString(),
            "created" to Date(),
            "creator" to userModel.documentId,
            "deadline" to deadline,
            "description" to binding.badgeDescription.text.toString(),
            "editors" to selectedEditors,
            "icon" to getString(R.string.under_construction_project_icon),
            "name" to binding.badgeName.text.toString(),
            "overall_progress" to 0,
            "mandatory" to false

        )
        firestore.collection(Collections.badges)
            .document(args.badge.id)
            .update(editedBadge as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot edited with ID: ${args.badge.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error editing document", e)
            }

        return true
    }
}