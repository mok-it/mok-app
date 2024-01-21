package mok.it.app.mokapp.fragments

import android.os.Bundle
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

    private val binding get() = _binding!!
    private var _binding: FragmentMemberBinding? = null
    private val args: MemberFragmentArgs by navArgs()
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
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
        for (category in Category.values()) {
            viewModel.getUserBadgeCountByCategory(args.user, category)
                .observe(viewLifecycleOwner) { badgeData ->
                    val badgeTextView = TextView(context)
                    badgeTextView.text = getString(
                        R.string.badge_count,
                        category.name,
                        badgeData.finishedProjectCount
                    )
                    sumOfBadges += badgeData.finishedProjectBadgeSum
                    numberOfAllProjects += badgeData.finishedProjectCount
                    binding.badgeContainer.addView(badgeTextView)
                }
        }

        binding.collectedBadgesSummary.text =
            getString(R.string.collectedBadgesSummary, sumOfBadges, numberOfAllProjects)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}