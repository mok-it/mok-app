package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.viewmodels.MemberViewModel

class MemberFragment : Fragment() {

    private val viewModel by viewModels<MemberViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_member, container, false)
    }

}