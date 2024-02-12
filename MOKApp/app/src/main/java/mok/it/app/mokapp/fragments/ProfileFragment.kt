package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileFragment()
    }

    private fun loadProfileFragment() {
        val childFragment: Fragment = MemberFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", userModel)
        childFragment.arguments = bundle
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, childFragment).commit()
    }
}