package mok.it.app.mokapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_badge_all_members_dialog.*
import kotlinx.android.synthetic.main.fragment_badge_all_members_dialog.view.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.MembersAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class BadgeAllMemberDialogFragment(val memberUsers: ArrayList<User>, val listener: MembersAdapter.MemberClickedListener) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_badge_all_members_dialog, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    fun initRecyclerView(){
        recyclerView.adapter = MembersAdapter(memberUsers, listener)
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }
}