package mok.it.app.mokapp.feature.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mok.it.app.mokapp.firebase.service.MiscService
import mok.it.app.mokapp.model.Link

class LinksViewModel : ViewModel() {
    val links: LiveData<List<Link>> = MiscService.getLinks()
}