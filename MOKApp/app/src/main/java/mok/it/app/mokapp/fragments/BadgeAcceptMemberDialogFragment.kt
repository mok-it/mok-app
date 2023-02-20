package mok.it.app.mokapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R

class BadgeAcceptMemberDialogFragment : DialogFragment() {

    companion object {
        const val acceptDialogResultKey = "acceptDialogResultKey"
    }

    private val args: BadgeAcceptMemberDialogFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.did_they_complete) + " ${args.name} ?")
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    acceptDialogResultKey, true
                )
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .setCancelable(true)
            .create()
}