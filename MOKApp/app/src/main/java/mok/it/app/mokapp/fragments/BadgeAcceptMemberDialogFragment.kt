package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_badge_accept_member_dialog.*
import kotlinx.android.synthetic.main.fragment_badge_accept_member_dialog.view.*
import mok.it.app.mokapp.R

class BadgeAcceptMemberDialogFragment : DialogFragment() {
    companion object {
        const val acceptDialogResultKey = "accept"
    }

    private val args: BadgeAcceptMemberDialogFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.fragment_badge_accept_member_dialog, container, false)
        rootView.OkButton.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                acceptDialogResultKey, args.user.documentId
            )
            dismiss()
        }
        rootView.CancelButton.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText.text = args.user.name
    }
}