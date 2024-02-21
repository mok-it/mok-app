package mok.it.app.mokapp.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.databinding.FragmentFilterDialogBinding
import mok.it.app.mokapp.model.Filter

class FilterDialogFragment : DialogFragment() {
    companion object {
        const val filterResultKey = "filter"
    }

    private val args: FilterDialogFragmentArgs by navArgs()
    private var filter: Filter = Filter()
    private lateinit var binding: FragmentFilterDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        binding.OkButton.setOnClickListener {
            setFilter()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                filterResultKey,
                filter
            )
            dismiss()
        }
        binding.CancelButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filter = args.filter ?: Filter()
        binding.switchAchieved.isChecked = filter.achieved
        binding.switchMandatory.isChecked = filter.mandatory
        binding.switchJoined.isChecked = filter.joined
        binding.switchEdited.isChecked = filter.edited
    }

    private fun setFilter() {
        filter.achieved = binding.switchAchieved.isChecked
        filter.mandatory = binding.switchMandatory.isChecked
        filter.joined = binding.switchJoined.isChecked
        filter.edited = binding.switchEdited.isChecked
    }
}