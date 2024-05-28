package mok.it.app.mokapp.feature.links

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.ChipTextFieldState
import mok.it.app.mokapp.firebase.service.MiscService
import mok.it.app.mokapp.model.Link
import mok.it.app.mokapp.utility.Utility.unaccent

class LinksViewModel : ViewModel() {
    private val links: LiveData<List<Link>> = MiscService.getLinks()

    val filteredLinks
        get() = links.map { links ->
            val cleanSearchQuery = searchQuery.value.trim().unaccent()
            links.filter { link ->
                isLinkMatched(
                    link, cleanSearchQuery, chipState.value
                )
            }.sortedWith(compareBy({ it.category }, { it.title }))
        }

    private fun isLinkMatched(
        link: Link,
        cleanSearchQuery: String,
        chipState: ChipTextFieldState<Chip>,
    ): Boolean {
        val cleanSearchWords =
            chipState.chips.map { it.text.trim().unaccent() } + cleanSearchQuery.trim().unaccent()

        return cleanSearchWords.all {
            link.title.unaccent().contains(it, ignoreCase = true) || link.category.unaccent()
                .contains(it, ignoreCase = true)
        }
    }

    private val _searchQuery = mutableStateOf("")
    val searchQuery get() = _searchQuery
    private val _chipState = mutableStateOf(ChipTextFieldState<Chip>())
    val chipState get() = _chipState

    fun onSearchValueChange(value: String) {
        _searchQuery.value = value
    }
}