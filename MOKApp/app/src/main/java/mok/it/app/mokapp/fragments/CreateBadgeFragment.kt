package mok.it.app.mokapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_create_badge.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity.Companion.userModel
import mok.it.app.mokapp.model.User
import java.util.*
import kotlin.collections.ArrayList


/**
 * When the 'Add' button is pressed in the badge list, a dialog opens up, prompting data
 * for a new badge. After all required fields are complete, the user can upload this new
 * badge to the server.
 */

class CreateBadgeFragment(val category: String) : DialogFragment() {

    lateinit var nameTIET: TextInputEditText
    lateinit var descriptionTIET: TextInputEditText
    lateinit var iconSelectCard: CardView
    lateinit var closeButton: ImageButton
    lateinit var createButton: Button

    lateinit var users: ArrayList<User>
    val userCollectionPath: String = "/users"
    val firestore = Firebase.firestore

    lateinit var names: Array<String>
    lateinit var checkedNames: BooleanArray
    var selectedEditors: ArrayList<String> = ArrayList()

    private val isNameRequired = true
    private val isDescriptionRequired = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_badge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            nameTIET = findViewById(R.id.badge_name)
            descriptionTIET = findViewById(R.id.badge_description)
            iconSelectCard = findViewById(R.id.icon_select_card)
            closeButton = findViewById(R.id.close_button)
            createButton = findViewById(R.id.create_button)

            closeButton.setOnClickListener {
                onCloseButtonPressed()
            }

            createButton.setOnClickListener {
                onCreateBadgePressed()
            }

            editorSelect.setOnClickListener {
                getUsers()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog

        dialog?.also {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT // TODO fix tall layout scrolling
            it.window?.setLayout(width, height)
            isCancelable = false
        }

        selectedEditors = ArrayList()
        selectedEditors.add(userModel.documentId)
    }

    /**
     * Called if the dialog needs to be closed.
     */
    private fun closeDialog() {
        dialog?.dismiss() ?: run {
            Log.w(TAG, "Can not close dialog, it is null.")
        }
    }

    /**
     * Called if the user wants to close the dialog.
     */
    private fun onCloseButtonPressed() {

        if (isBlank()) {
            closeDialog()
            return
        }

        // The user has unsaved changes, thus warning them before closing the dialog

        AlertDialog.Builder(context)
            .setCancelable(false)
            .setMessage(R.string.unsaved_changes)
            .setPositiveButton(
                R.string.discard
            ) { _, _ -> closeDialog() }
            .setNegativeButton(R.string.edit, null)
            .create()
            .show()

    }

    /**
     *  Creates a new badge in the database.
     *  @return true if successful
     */
    private fun commitNewBadgeToDatabase(): Boolean {
        Log.d("Create name", nameTIET.text.toString())
        Log.d("Create desc", descriptionTIET.text.toString())
        Log.d("Create creator", userModel.documentId)
        Log.d("Create category", category)
        val deadline = Date(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
        Log.d("Create date", deadline.toString())
        Log.d("Create editors", selectedEditors.toString())

        val newBadge = hashMapOf(
            "category" to category,
            "created" to Date(),
            "creator" to userModel.documentId,
            "deadline" to deadline,
            "description" to descriptionTIET.text.toString(),
            "editors" to selectedEditors,
            "icon" to "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef",
            "name" to nameTIET.text.toString(),
            "overall_progress" to 0,
            "mandatory" to false

        )
        firestore.collection("/projects")
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
    private fun onCreateBadgePressed() {
        val shouldCloseDialog = onCreateBadge()

        if (shouldCloseDialog) {
            closeDialog()
        }
    }

    /**
     * TODO Known bug: the contents of an input field seems to always register as blank
     * if the cursor is in it
     *
     * @return false if there exists any user-given piece of information in the dialog
     */
    private fun isBlank() = when {

        !nameTIET.text.isNullOrBlank() -> false
        !descriptionTIET.text.isNullOrBlank() -> false

        else -> true
    }

    /**
     * @return if all required fields are complete. If something is missing, can warn the user.
     */
    private fun isComplete(showWarning: Boolean): Boolean = when {
        isNameRequired && nameTIET.text.isNullOrBlank() -> {
            if (showWarning) toast(R.string.badge_name_is_required) // TODO snackbar
            false
        }

        isDescriptionRequired && descriptionTIET.text.isNullOrBlank() -> {
            if (showWarning) toast(R.string.badge_description_is_required) // TODO snackbar
            false
        }

        else -> true
    }

    fun toast(textResource: Int) = Toast.makeText(
        context,
        textResource,
        Toast.LENGTH_SHORT
    ).show()

    private fun getUsers(){
        users = ArrayList()

        firestore.collection(userCollectionPath)
            .whereArrayContains("categories", category)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        users.add(document.toObject(User::class.java))
                        Log.d("Users", users.toString())
                    }
                    names = Array(users.size){i->users[i].name}
                    checkedNames = BooleanArray(users.size){false}
                    initEditorsDialog()
                }
            }
    }

    private fun initEditorsDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Válassz kezelőt!")
        builder.setMultiChoiceItems(names, checkedNames){_, which, isChecked ->
            checkedNames[which] = isChecked
        }
        builder.setPositiveButton("Ok"){_, _ ->

            for (i in names.indices){
                if (checkedNames[i]){
                    Log.d("Selected", names[i])
                    selectedEditors.add(users[i].documentId)
                }
            }
        }
        builder.setNegativeButton("Mégsem"){dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    companion object {
        val TAG = "CreateBadgeFragment"
        fun newInstance() = CreateBadgeFragment("")
    }
}