package mok.it.app.mokapp.feature.project_import_export

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.utility.Utility.TAG

class ProjectImportExportFragment : Fragment() {
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.e(TAG, uri?.path ?: "null")
            viewModel.onEvent(ImportExportEvent.ImportSelection(uri))
        }


    private val viewModel: ProjectImportExportViewModel by viewModels {
        ProjectImportExportViewModelFactory(getContent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            MokAppTheme {
                ImportExportScreen(viewModel)
            }
        }
    }
}

@Composable
fun ImportExportScreen(viewModel: ProjectImportExportViewModel) {
    Surface {
        Column {
            Button(onClick = { viewModel.onEvent(ImportExportEvent.Import) }) {
                Text("Importálás")
            }
        }
    }
}