package mok.it.app.mokapp.feature.project_import_export

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mok.it.app.mokapp.model.ExportProject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.ui.compose.projects.ProjectCard
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.utility.Utility.TAG
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.io.Writer
import java.sql.Date

class ProjectImportExportFragment : Fragment() {
    private val selectFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.e(TAG, uri?.path ?: "null")
            uri?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.use {
                    val projects =
                        try {
                            Log.e(TAG, "Reading CSV")
                            readCsv(it)
                        } catch (_: Exception) {
                            Log.e(TAG, "Error reading CSV, using dummy data")
                            listOf(
                                Project(
                                    id = "1",
                                    category = "category",
                                    created = Date(0),
                                    creator = "creator",
                                    deadline = Date(0),
                                    description = "description",
                                    icon = "icon",
                                    name = "name",
                                    maxBadges = 1,
                                    projectLeader = "projectLeader"
                                )
                            )
                        }
                    viewModel.onEvent(ImportExportEvent.Import(projects))
                }
            }
        }
    private val createFile =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) {
            it?.let { uri ->
                lifecycleScope.launch {
                    requireContext().contentResolver.openOutputStream(uri)?.writer()
                        ?.use { writer ->
                            Log.e(TAG, "Writing CSV")
                            viewModel.existingProjects.collectLatest { projects ->
                                for (x in projects) {
                                    Log.e(TAG, "${x.name}, ${x.members}")
                                }
                                Log.e(TAG, "${projects.size}")

                                writer.writeCsv(projects)
                            }
                        }
                }
            }
        }


    private val viewModel: ProjectImportExportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        Log.d(TAG, "onCreateView")
        setContent {
            MokAppTheme {
                ImportExportScreen(
                    viewModel.importedProjects,
                    { selectFile.launch("*/*") },
                    { createFile.launch("projects.csv") },
                )
            }
        }
    }
}

private fun readCsv(inputStream: InputStream): List<Project> =
    CSVParser(inputStream.reader(), CSVFormat.DEFAULT)
        .drop(1) // Dropping the header
        .map {
            Project(
                id = it[0],
                category = it[1],
                created = Date.valueOf(it[2]),
                creator = it[3],
                deadline = Date.valueOf(it[4]),
                description = it[5],
                icon = it[6],
                name = it[7],
                maxBadges = it[8].toInt(),
                projectLeader = it[9]
            )
        }

fun Writer.writeCsv(projects: List<ExportProject>) {
    CSVFormat.DEFAULT.print(this).apply {
        printRecord(
            "név",
            "kategória",
            "létrehozás dátuma",
            "létrehozó",
            "projektvezető",
            "határidő",
            "leírás",
            "Ikon URL",
            "tagok",
            "előrehaladás",
            "kommentek száma",
            "szerezhető mancsok száma",
            "összes kiosztott mancs"
        )
        projects.forEach { project ->
            printRecord(
                project.name,
                project.category,
                project.created,
                project.creator,
                project.projectLeader,
                project.deadline,
                project.description,
                project.icon,
                project.members,
                project.overallProgress,
                project.commentCount,
                project.maxBadges,
                project.totalEarnedBadges
            )
        }
    }
}

@Composable
fun ImportExportScreen(
    projects: List<Project>,
    selectFile: () -> Unit,
    createFile: () -> Unit,
) {
    val projects = remember { projects }
    Surface {
        if (projects.isEmpty()) {
            Column {
                Button(onClick = selectFile) {
                    Text("Importálás")
                }
                Button(onClick = createFile) {
                    Text("Exportálás")
                }
            }
        } else {
            LazyColumn {
                items(projects) { project ->
                    ProjectCard(project, {})
                }
            }
        }
    }
}