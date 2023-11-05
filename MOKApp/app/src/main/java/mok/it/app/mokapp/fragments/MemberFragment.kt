package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        loadMutualBadges()
    }

    private fun loadMutualBadges() {

    }

    private fun loadBadgeCounts() {
        viewModel.getUserBadgeCountByCategory(args.user, Category.UNIVERZALIS)
            .observe(viewLifecycleOwner) {
                binding.badgeCountUniversal.text =
                    getString(R.string.badge_count, Category.UNIVERZALIS, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.SZERVEZETFEJLESZTES)
            .observe(viewLifecycleOwner) {
                binding.badgeCountSzervezetfejlesztes.text =
                    getString(R.string.badge_count, Category.SZERVEZETFEJLESZTES, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.FELADATSOR)
            .observe(viewLifecycleOwner) {
                binding.badgeCountFeladatsor.text =
                    getString(R.string.badge_count, Category.FELADATSOR, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.PEDAGOGIA)
            .observe(viewLifecycleOwner) {
                binding.badgeCountPedagogia.text =
                    getString(R.string.badge_count, Category.PEDAGOGIA, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.MEDIAESDIY)
            .observe(viewLifecycleOwner) {
                binding.badgeCountMedia.text =
                    getString(R.string.badge_count, Category.MEDIAESDIY, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.IT)
            .observe(viewLifecycleOwner) {
                binding.badgeCountIt.text =
                    getString(R.string.badge_count, Category.IT, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, Category.TABORIPROGRAMESELOKESZITES)
            .observe(viewLifecycleOwner) {
                binding.badgeCountTaboriprogram.text =
                    getString(R.string.badge_count, Category.TABORIPROGRAMESELOKESZITES, it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}