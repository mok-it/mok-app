package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.composables.achievements.AchievementCard
import mok.it.app.mokapp.fragments.viewmodels.AchievementListViewModel
import mok.it.app.mokapp.utility.Utility.TAG

class AchievementsFragment : Fragment() {
    private val viewModel: AchievementListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            Log.e(TAG, "AchievementsFragment: started")
            setContent {
                val achievements by viewModel.achievements.observeAsState(emptyList())
                Log.e(TAG, "Achievements size: ${achievements.size}")
                Column {
                    Text(text = "Achievements")
                    LazyColumn {
                        items(achievements) { achievement ->
                            AchievementCard(owned = false, achievement = achievement)
                        }
                    }
                }
            }
        }
}