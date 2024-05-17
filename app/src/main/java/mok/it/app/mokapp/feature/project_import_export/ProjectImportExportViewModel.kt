package mok.it.app.mokapp.feature.project_import_export

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mok.it.app.mokapp.utility.Utility.TAG

class ProjectImportExportViewModel(val getFile: ActivityResultLauncher<String>) :
    ViewModel() {


    fun onEvent(event: ImportExportEvent) {
        when (event) {

            is ImportExportEvent.Import -> {
                val x = getFile.launch("*/*")
            }

            is ImportExportEvent.ImportSelection -> {
                Log.e(TAG, "Selected file: ${event.uri?.path ?: "asdf"}")
            }

            is ImportExportEvent.Export -> {
            }
        }
    }
}

class ProjectImportExportViewModelFactory(private val getFile: ActivityResultLauncher<String>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectImportExportViewModel::class.java)) {
            return ProjectImportExportViewModel(getFile) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


sealed class ImportExportEvent {
    data object Import : ImportExportEvent()
    data class ImportSelection(val uri: Uri?) : ImportExportEvent()
    data object Export : ImportExportEvent()
}