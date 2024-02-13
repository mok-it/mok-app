package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentMemberBinding
import mok.it.app.mokapp.fragments.viewmodels.MemberViewModel
import mok.it.app.mokapp.model.Category

class MemberFragment : Fragment() {

    private lateinit var binding: FragmentMemberBinding
    private val args: MemberFragmentArgs by navArgs()
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        loadDataIntoControls()
        return binding.root
    }

    private fun loadDataIntoControls() {
        binding.name.text = args.user.name
        binding.nickname.text = args.user.nickname.ifEmpty { "Nincs becenév megadva" }
        binding.phoneNumber.text =
            args.user.phoneNumber.ifEmpty { getString(R.string.no_phone_number) }
        binding.email.text = args.user.email
        viewModel.loadImage(binding.profilePicture, args.user.photoURL)

        loadBadgeCounts()
    }

    private fun loadBadgeCounts() {
        var sumOfBadges = 0
        var numberOfAllProjects = 0
        for (category in Category.entries) {
            Log.d("MANCSAIM", Category.entries.toTypedArray().toString())
            viewModel.getUserBadgeCountByCategory(args.user, category)
                .observe(viewLifecycleOwner) { badgeData ->
                    if (badgeData.finishedProjectBadgeSum != 0){
                        val badgeTextView = TextView(context)
                        badgeTextView.text = getString(
                            R.string.project_count,
                            category.toString(),
                            badgeData.finishedProjectBadgeSum
                        )
                        sumOfBadges += badgeData.finishedProjectBadgeSum
                        numberOfAllProjects += badgeData.finishedProjectCount
                        binding.badgeContainer.addView(badgeTextView)
                    }
                    binding.collectedBadgesSummary.text =
                        getString(R.string.collectedBadgesSummary, sumOfBadges)
                }
        }


    }
}