package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.service.UserService
import java.util.Calendar
import java.util.Date

@Suppress("DEPRECATION")
class EditBadgeFragment : CreateBadgeFragment() {

    private val args: EditBadgeFragmentArgs by navArgs()

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
        binding.datePicker.updateDate(
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        )
        badgeValue = args.badge.value
        binding.tvBadgeValue.text = badgeValue.toString()
        binding.textViewTitle.text = getString(R.string.edit_badge_text)
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
        val deadline = Date(binding.datePicker.year - 1900, binding.datePicker.month, binding.datePicker.dayOfMonth)
        val editedBadge = hashMapOf(
            "category" to binding.badgeMcs.text.toString(),
            "deadline" to deadline,
            "description" to binding.badgeDescription.text.toString(),
            "editors" to selectedEditors,
            "name" to binding.badgeName.text.toString(),
            "value" to badgeValue,
            //TODO: update icon if a new one was selected, otherwise leave it untouched!
        )
        firestore.collection(Collections.badges)
            .document(args.badge.id)
            .update(editedBadge as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot edited with ID: ${args.badge.id}")
                UserService.capProjectBadges(args.badge.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error editing document", e)
            }

        return true
    }
}