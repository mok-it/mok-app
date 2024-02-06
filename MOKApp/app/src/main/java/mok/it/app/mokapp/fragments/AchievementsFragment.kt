package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import mok.it.app.mokapp.model.Achievement

class AchievementsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        super.onCreateView(inflater, container, savedInstanceState)
        return ComposeView(requireContext()).apply {
            setContent {
                Default()
            }
        }
    }

    @Composable
    fun Default() {
        Card {
            Text("Hello, World!")
        }
    }

    @Composable
    fun AchievementCard(achievement: Achievement) {
    Card {
        Column {
            Row {
                AsyncImage(
                    model = "https://firebasestorage.googleapis.com/v0/b/mokapp-51f86.appspot.com/o/under_construction_badge.png?alt=media&token=3341868d-5aa8-4f1b-a8b6-f36f24317fef",
                    contentDescription = "Under construction",
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .clip(CircleShape)
                )
                Text(achievement.name)
            }
            Text(achievement.description)
        }
    }
    }

    @Preview
    @Composable
    fun DefaultPreview() {
        Default()
    }

}
