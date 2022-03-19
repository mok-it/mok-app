package mok.it.app.mokapp.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import mok.it.app.mokapp.R


/**
 * When the 'Add' button is pressed in the badge list, a dialog opens up, prompting data
 * for a new badge. After all required fields are complete, the user can upload this new
 * badge to the server.
 */

class CreateBadgeFragment : DialogFragment() {

    lateinit var nameTIET: TextInputEditText
    lateinit var descriptionTIET: TextInputEditText
    lateinit var iconSelectCard: CardView
    lateinit var closeButton: ImageButton
    lateinit var createButton: Button

    private val isNameRequired = true
    private val isDescriptionRequired = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_badge, container, false);
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
        // TODO upload badge metadata to firebase
        toast(R.string.not_implemented)
        return false
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

        nameTIET.text.isNullOrBlank() -> true
        descriptionTIET.text.isNullOrBlank() -> true

        else -> false
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

    companion object {
        val TAG = "CreateBadgeFragment"
        fun newInstance() = CreateBadgeFragment()
    }
}