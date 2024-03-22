package mok.it.app.mokapp.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.databinding.FragmentProjectMembersDialogBinding
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.setFullScreen

class ProjectMembersDialogFragment : DialogFragment() {
    private val args: ProjectMembersDialogFragmentArgs by navArgs()

    private val binding get() = _binding!!
    private var _binding: FragmentProjectMembersDialogBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectMembersDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = MembersAdapter(args.users, this)
        binding.recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    fun navigateToMemberFragment(user: User) {
        val action =
            ProjectMembersDialogFragmentDirections.actionGlobalMemberFragment(
                user
            )
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}