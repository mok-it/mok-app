package mok.it.app.mokapp.feature.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.model.Link
import mok.it.app.mokapp.service.MiscService

class LinksViewModel : ViewModel() {
    val links: LiveData<List<Link>> = MiscService.getLinks()
}