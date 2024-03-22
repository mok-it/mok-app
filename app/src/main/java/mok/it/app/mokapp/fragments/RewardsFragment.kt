package mok.it.app.mokapp.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.CardRewardBinding
import mok.it.app.mokapp.databinding.FragmentRewardsBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.RewardsService
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.recyclerview.RewardViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility
import kotlin.math.absoluteValue

class RewardsFragment : Fragment() {
    lateinit var adapter: FirestoreRecyclerAdapter<*, *>
    private lateinit var _binding: FragmentRewardsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        binding.pointsText.text =
            getString(R.string.my_badges_count, userModel.projectBadges.values.sum())
        binding.spentPointsText.text = getString(R.string.my_spent_badges_count, userModel.points)
        initializeAdapter()
    }

    private fun initializeAdapter() {
        val options: FirestoreRecyclerOptions<Reward?> = FirestoreRecyclerOptions.Builder<Reward>()
            .setQuery(
                RewardsService.getRewardsQuery(),
                Reward::class.java
            )
            .build()

        adapter =
            object : FirestoreRecyclerAdapter<Reward?, RewardViewHolder?>(options) {
                var context: Context? = null

                override fun onCreateViewHolder(parent: ViewGroup, i: Int) = RewardViewHolder(
                    CardRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )

                override fun onBindViewHolder(
                    holder: RewardViewHolder,
                    position: Int,
                    model: Reward
                ) {
                    Utility.loadImage(holder.binding.rewardImage, model.icon, requireContext())
                    holder.binding.rewardName.text = model.name
                    holder.binding.rewardPrice.text = model.price.toString()
                    holder.binding.rewardQuantityLeft.text =
                        getString(R.string.quantity, model.quantity)

                    // if the user has enough badges AND there is still some of the reward available:
                    if (userModel.projectBadges.values.sum() - userModel.points.absoluteValue >= model.price &&
                        model.quantity > 0
                    ) {
                        holder.binding.requestButton.isEnabled = true
                    }

                    if (userModel.requestedRewards.contains(model.documentId)) {
                        holder.binding.requestButton.visibility = View.GONE
                        holder.binding.achievedText.visibility = View.VISIBLE
                    }
                    holder.binding.requestButton.setOnClickListener {
                        requestReward(model)
                    }
                }

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    context = recyclerView.context
                }

                fun loadImage(imageView: ImageView, imageURL: String): Boolean {
                    return try {
                        Picasso.get().apply {
                            load(imageURL).into(imageView)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
            }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    private fun requestReward(reward: Reward) {
        (activity as Activity).let {
            MaterialDialog.Builder(it)
                .setTitle("Biztosan kéred a jutalmat?")
                .setMessage("${reward.price} mancs kerül majd levonásra tőled.")
                .setPositiveButton(
                    it.getString(R.string.ok), R.drawable.ic_check
                ) { dialogInterface, _ ->
                    RewardsService.acceptRewardRequest(reward) {
                        context?.let { context ->
                            FirebaseUserObject.refreshCurrentUserAndUserModel(context) {
                                updateUI()
                                adapter.startListening()
                            }
                        }
                    }
                    dialogInterface.dismiss()
                }
                .setNegativeButton(
                    "Mégsem", R.drawable.ic_close_24
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .build()
                .show()
        }
    }
}