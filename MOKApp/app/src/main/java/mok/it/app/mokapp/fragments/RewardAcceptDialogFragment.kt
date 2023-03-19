package mok.it.app.mokapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Reward

class RewardAcceptDialogFragment(private val listener: RewardAcceptListener, private val reward: Reward) : DialogFragment() {

    interface RewardAcceptListener {
        fun rewardAccepted(reward: Reward)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Biztosan kéred a jutalmat?")
            .setMessage("${reward.price} pontba kerül.")
            .setPositiveButton(R.string.ok) { _, _ ->
                    listener.rewardAccepted(reward)
                }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
}