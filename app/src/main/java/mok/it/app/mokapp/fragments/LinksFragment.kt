package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import com.dokar.chiptextfield.rememberChipTextFieldState
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.SearchField
import mok.it.app.mokapp.fragments.viewmodels.LinksViewModel
import mok.it.app.mokapp.model.Link
import mok.it.app.mokapp.utility.Utility.unaccent
import java.util.Locale

class LinksFragment : Fragment() {
    private val viewModel: LinksViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                LinksFragment()
            }
        }

    @SuppressLint("NotConstructor")
    @Composable
    fun LinksFragment() {
        val chipState = rememberChipTextFieldState<Chip>()
        var searchQuery by remember { mutableStateOf("") }
        val filteredLinks = getFilteredLinks(searchQuery, chipState)

        Column {
            SearchField(
                searchQuery = searchQuery,
                chipState = chipState,
                onValueChange = { searchQuery = it },
            )
            if (filteredLinks.isEmpty()) {
                Text(
                    text = "Nincsenek a feltételeknek megfelelő linkek",
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredLinks) { link ->
                        LinkCard(link = link, onLinkClick = {
                            val openURL = Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(link.url)
                            context?.startActivity(openURL)
                        })
                    }
                }
            }
        }
    }

    @Composable
    private fun getFilteredLinks(
        searchQuery: String,
        chipState: ChipTextFieldState<Chip>
    ): List<Link> {
        val cleanSearchQuery = searchQuery.trim().unaccent()
        return viewModel.links.observeAsState().value
            ?.filter { link -> isLinkMatched(link, cleanSearchQuery, chipState) }
            .orEmpty()
            .sortedWith(compareBy({ it.category }, { it.title }))
    }

    private fun isLinkMatched(
        link: Link,
        cleanSearchQuery: String,
        chipState: ChipTextFieldState<Chip>
    ): Boolean {
        val cleanSearchWords =
            chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        return cleanSearchWords.all {
            link.title.unaccent().contains(it, ignoreCase = true)
                    || link.category.unaccent().contains(it, ignoreCase = true)
        }
    }

    @Composable
    fun LinkCard(link: Link, onLinkClick: (Link) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onLinkClick(link) },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_link),
                    contentDescription = "Link icon",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = link.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = link.category.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
