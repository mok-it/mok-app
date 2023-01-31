package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_badge_accept_member_dialog.view.*
import kotlinx.android.synthetic.main.fragment_filter_dialog.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Filter

class FilterDialogFragment : DialogFragment() {
    private val args: FilterDialogFragmentArgs by navArgs()
    private var filter: Filter = Filter()

    companion object {
        const val filterResultKey = "filter"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter_dialog, container, false)
        rootView.OkButton.setOnClickListener {
            setFilter()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                filterResultKey,
                filter
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
        filter = args.filter ?: Filter()
        switchAchieved.isChecked = filter.achieved
        switchMandatory.isChecked = filter.mandatory
        switchJoined.isChecked = filter.joined
        switchEdited.isChecked = filter.edited
    }

    private fun setFilter() {
        filter.achieved = switchAchieved.isChecked
        filter.mandatory = switchMandatory.isChecked
        filter.joined = switchJoined.isChecked
        filter.edited = switchEdited.isChecked
    }
}