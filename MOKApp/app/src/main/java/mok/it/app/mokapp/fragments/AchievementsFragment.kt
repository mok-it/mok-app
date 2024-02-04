package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class AchievementsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.e("asdf", "creating view ACSIK")

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

    @Preview
    @Composable
    fun DefaultPreview() {
        Default()
    }

}
