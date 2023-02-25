package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_badge_all_members_dialog.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class BadgeAllMemberDialogFragment(
    private val memberUsers: ArrayList<User>,
    private val listener: MembersAdapter.MemberClickedListener,
    private val canEdit: Boolean
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_badge_all_members_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.adapter = MembersAdapter(memberUsers, listener, canEdit)
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }
}