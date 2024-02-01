package mok.it.app.mokapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_create_badge.datePicker
import kotlinx.android.synthetic.main.fragment_create_badge.tvBadgeValue
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentCreateBadgeBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.MyFirebaseMessagingService
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import java.util.Date


/**
 * When the 'Add' button is pressed in the badge list, a dialog opens up, prompting data
 * for a new badge. After all required fields are complete, the user can upload this new
 * badge to the server.
 */

@Suppress("DEPRECATION")
open class CreateBadgeFragment : DialogFragment() {

    private val args: CreateBadgeFragmentArgs by navArgs()

    val binding get() = _binding!!
    private var _binding: FragmentCreateBadgeBinding? = null

    var users: ArrayList<User> = ArrayList()
    val userCollectionPath: String = "/users"
    val firestore = Firebase.firestore

    lateinit var names: Array<String>
    lateinit var checkedNames: BooleanArray
    var selectedEditors: MutableList<String> = mutableListOf()

    private val isNameRequired = true
    private val isDescriptionRequired = true
    protected var badgeValue = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBadgeBinding.inflate(inflater, container, false)
        initializeDropdown()
        return binding.root
    }

    private fun initializeDropdown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.mcs_dropdown_item, Category.values())
        binding.badgeMcs.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener {
            onCloseButtonPressed()
        }

        binding.createButton.setOnClickListener {
            onCreateBadgePressed()
        }

        binding.editorSelect.setOnClickListener {
            getUsers()
        }

        binding.btnDecreaseValue.setOnClickListener {
            if (badgeValue > 1) {
                tvBadgeValue.text = (--badgeValue).toString()
            } else {
                toast(R.string.value_at_least_one)
            }
        }

        binding.btnIncreaseValue.setOnClickListener {
            tvBadgeValue.text = (++badgeValue).toString()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog

        dialog?.also {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
            isCancelable = false
        }

        selectedEditors = mutableListOf(userModel.documentId)
    }

    /**
     * Called if the user wants to close the dialog.
     */
    private fun onCloseButtonPressed() {

        if (isBlank()) {
            findNavController().navigateUp()
            return
        }

        // The user has unsaved changes, thus warning them before closing the dialog
        (context as Activity).let {
            MaterialDialog.Builder(it)
                .setTitle(it.getString(R.string.unsaved_changes))
                .setNegativeButton(it.getString(R.string.discard)) { dialogInterface, _ ->
                    findNavController().navigateUp()
                    dialogInterface.dismiss()
                }
                .setPositiveButton(it.getString(R.string.edit)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build()
                .show()
        }
    }

    /**
     *  Creates a new badge in the database.
     *  @return true if successful
     */
    private fun commitNewBadgeToDatabase(): Boolean {
        Log.d("Create name", binding.badgeName.text.toString())
        Log.d("Create desc", binding.badgeDescription.text.toString())
        Log.d("Create creator", userModel.documentId)
        Log.d("Create category", args.category.toString())
        val deadline = Date(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
        Log.d("Create date", deadline.toString())
        Log.d("Create editors", selectedEditors.toString())
        Log.d("Create value", binding.tvBadgeValue.text.toString())

        val newBadge = hashMapOf(
            "category" to binding.badgeMcs.text.toString(),
            "created" to Date(),
            "creator" to userModel.documentId,
            "deadline" to deadline,
            "description" to binding.badgeDescription.text.toString(),
            "editors" to selectedEditors,
            "icon" to "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef",
            "name" to binding.badgeName.text.toString(),
            "overall_progress" to 0,
            "value" to badgeValue,
            "mandatory" to false

        )
        firestore.collection(Collections.badges)
            .add(newBadge)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        return true
    }

    /**
     * Called if the Create button is pressed in the dialog.
     * @return whether the dialog should be closed
     */
    private fun onCreateBadge(): Boolean {
        if (!isComplete(true)) {
            return false
        }

        val success = commitNewBadgeToDatabase()

        MyFirebaseMessagingService.sendNotificationToUsers(
            "Új mancs lett létrehozva",
            "${userModel.name} egy új mancsot hozott létre az alábbi névvel: ${binding.badgeName.text}",
            users.filterNot { it.documentId == userModel.documentId }
        )

        return if (success) {
            true
        } else {
            toast(R.string.error_occurred)
            false
        }
    }

    /**
     * Called if the user wants to create the badge.
     */
    protected open fun onCreateBadgePressed() {
        val shouldCloseDialog = onCreateBadge()

        if (shouldCloseDialog) {
            findNavController().navigateUp()
        }
    }

    /**
     * @return false if there exists any user-given piece of information in the dialog
     */
    private fun isBlank() = when {

        !binding.badgeName.text.isNullOrBlank() -> false
        !binding.badgeDescription.text.isNullOrBlank() -> false

        else -> true
    }

    /**
     * @return if all required fields are complete. If something is missing, can warn the user.
     */
    private fun isComplete(showWarning: Boolean): Boolean = when {
        isNameRequired && binding.badgeName.text.isNullOrBlank() -> {
            if (showWarning) snackbar(R.string.badge_name_is_required)
            false
        }

        isDescriptionRequired && binding.badgeDescription.text.isNullOrBlank() -> {
            if (showWarning) snackbar(R.string.badge_description_is_required)
            false
        }

        else -> true
    }

    private fun snackbar(textResource: Int) = Snackbar.make(
        binding.root,
        textResource,
        Snackbar.LENGTH_SHORT
    ).show()

    fun toast(textResource: Int) = Toast.makeText(
        context,
        textResource,
        Toast.LENGTH_SHORT
    ).show()

    protected open fun getUsers() {
        users = ArrayList()

        firestore.collection(userCollectionPath)
            .whereArrayContains("categories", args.category.toString())
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        users.add(document.toObject(User::class.java))
                        Log.d("Users", users.toString())
                    }
                    names = Array(users.size) { i -> users[i].name }
                    checkedNames = BooleanArray(users.size) { false }
                    initEditorsDialog()
                }
            }
    }

    protected fun initEditorsDialog() {
        AlertDialog.Builder(context)
            .setTitle("Válassz kezelőt!")
            .setMultiChoiceItems(names, checkedNames) { _, which, isChecked ->
                checkedNames[which] = isChecked
            }
            .setPositiveButton("Ok") { _, _ ->
                if (!names.indices.isEmpty()) {
                    for (i in names.indices) {
                        if (checkedNames[i]) {
                            Log.d("Selected", names[i])
                            Log.d("Selected", users[i].documentId)
                            selectedEditors.add(users[i].documentId)
                        }
                    }
                }
            }
            .setNegativeButton("Mégsem") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "CreateBadgeFragment"
    }
}