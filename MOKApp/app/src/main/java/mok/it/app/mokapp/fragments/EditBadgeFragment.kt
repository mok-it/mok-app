package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_create_badge.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentCreateBadgeBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.User
import java.time.LocalDate
import java.util.*

class EditBadgeFragment : CreateBadgeFragment() {

    private val args: EditBadgeFragmentArgs by navArgs()

    private val binding get() = _binding!!
    private var _binding: FragmentCreateBadgeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBadgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.badgeName.setText(args.badge.name)
        binding.badgeDescription.setText(args.badge.description)
        val cal: Calendar = Calendar.getInstance()
        cal.time = args.badge.deadline
        datePicker.updateDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        binding.textViewTitle.text = getString(R.string.edit_badge_text)
        binding.createButton.text = getString(R.string.edit_text)

        selectedEditors = args.badge.editors.toMutableList()
        super.onViewCreated(view, savedInstanceState)
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
        val deadline = LocalDate.of(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
        val editedBadge = hashMapOf(
            "category" to args.badge.categoryEnum,
            "created" to Date(),
            "creator" to userModel.documentId,
            "deadline" to deadline,
            "description" to binding.badgeDescription.text.toString(),
            "editors" to selectedEditors,
            "icon" to getString(R.string.under_construction_badge_icon),
            "name" to binding.badgeName.text.toString(),
            "overall_progress" to 0,
            "mandatory" to false

        )
        firestore.collection("/projects")
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}