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
import mok.it.app.mokapp.model.BadgeCategory

class MemberFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentMemberBinding? = null
    private val args: MemberFragmentArgs by navArgs()
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        //TODO közös mancsok

        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.UNIVERZALIS)
            .observe(viewLifecycleOwner) {
                binding.badgeCountUniversal.text =
                    getString(R.string.badge_count, BadgeCategory.UNIVERZALIS, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.FELADATSOR)
            .observe(viewLifecycleOwner) {
                binding.badgeCountFeladatsor.text =
                    getString(R.string.badge_count, BadgeCategory.FELADATSOR, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.PEDAGOGIA)
            .observe(viewLifecycleOwner) {
                binding.badgeCountPedagogia.text =
                    getString(R.string.badge_count, BadgeCategory.PEDAGOGIA, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.KREATIV)
            .observe(viewLifecycleOwner) {
                binding.badgeCountKreativ.text =
                    getString(R.string.badge_count, BadgeCategory.KREATIV, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.IT)
            .observe(viewLifecycleOwner) {
                binding.badgeCountIt.text =
                    getString(R.string.badge_count, BadgeCategory.IT, it)
            }
        viewModel.getUserBadgeCountByCategory(args.user, BadgeCategory.GRAFIKA)
            .observe(viewLifecycleOwner) {
                binding.badgeCountGrafika.text =
                    getString(R.string.badge_count, BadgeCategory.GRAFIKA, it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}