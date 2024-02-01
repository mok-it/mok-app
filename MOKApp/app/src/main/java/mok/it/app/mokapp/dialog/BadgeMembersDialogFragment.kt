package mok.it.app.mokapp.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.databinding.FragmentBadgeMembersDialogBinding
import mok.it.app.mokapp.fragments.viewmodels.DetailsFragmentViewModel
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.setFullScreen

class BadgeMembersDialogFragment : DialogFragment() {
    private val args: BadgeMembersDialogFragmentArgs by navArgs()

    private val binding get() = _binding!!
    private var _binding: FragmentBadgeMembersDialogBinding? = null

    private val viewModel: DetailsFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgeMembersDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        initRecyclerView()
        viewModel.setMembers(args.users)
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = MembersAdapter(args.users, this)
        binding.recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    fun navigateToMemberFragment(user: User) {
        val action =
            BadgeMembersDialogFragmentDirections.actionGlobalMemberFragment(
                user
            )
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}