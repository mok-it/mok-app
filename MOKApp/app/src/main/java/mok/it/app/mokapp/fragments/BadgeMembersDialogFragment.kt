package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_badge_all_members_dialog.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.setFullScreen

class BadgeMembersDialogFragment : DialogFragment() {
    private val args: BadgeAllMemberDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_badge_all_members_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.adapter = MembersAdapter(args.users, args.canEdit)
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }
}