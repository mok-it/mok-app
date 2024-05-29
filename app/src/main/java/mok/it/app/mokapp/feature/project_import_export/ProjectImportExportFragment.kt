package mok.it.app.mokapp.feature.project_import_export

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mok.it.app.mokapp.model.ExportProject
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.ui.compose.AdminButton
import mok.it.app.mokapp.ui.compose.projects.ImportProjectCard
import mok.it.app.mokapp.ui.compose.theme.ExtendedTheme
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
                Log.i(TAG, "selected file at path: " + (uri?.path ?: "null"))
                uri?.let { uri ->
                    requireContext().contentResolver.openInputStream(uri)?.use {
                        Log.i(TAG, "Reading CSV")
                        viewModel.selectImport(
                                try {
                                    readCsv(it)
                                } catch (_: Exception) {
                                    null
                                }
                        )
                    }
                }
            }
    private val createFile =
            registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) {
                it?.let { uri ->
                    lifecycleScope.launch {
                        requireContext().contentResolver.openOutputStream(uri)?.writer()
                                ?.use { writer ->
                                    Log.i(TAG, "Writing CSV")
                                    viewModel.existingProjects.collectLatest { projects ->
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
        setContent {
            val importError by viewModel.importError
            MokAppTheme {
                ImportExportScreen(
                        viewModel.importedProjects,
                        importError,
                        { selectFile.launch("*/*") },
                        { viewModel.saveImport(); findNavController().popBackStack() },
                        { createFile.launch("projects.csv") },
                )
            }
        }
    }
}

private fun readCsv(inputStream: InputStream): List<Project> =
        CSVParser(inputStream.reader(), CSVFormat.DEFAULT)
                .map {
                    Project(
                            category = it[0],
                            deadline = try {
                                Date.valueOf(it[1])
                            } catch (_: Exception) {
                                Date(0)
                            },
                            description = it[2],
                            icon = it[3],
                            name = it[4],
                            maxBadges = it[5].toInt(),
                    )
                }

private fun Writer.writeCsv(projects: List<ExportProject>) {
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
        importedProjects: List<Project>,
        importError: Boolean,
        selectFile: () -> Unit,
        saveImport: () -> Unit,
        createFile: () -> Unit,
) {
    Surface {
        LazyColumn {
            if (importedProjects.isEmpty()) {
                item {
                    Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
		                            .fillMaxWidth()
		                            .padding(vertical = 8.dp)
                    ) {
                        AdminButton(
                                modifier = Modifier.weight(1f),
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Importálás",
                                onClick = selectFile
                        )
                        AdminButton(
                                modifier = Modifier.weight(1f),
                                imageVector = Icons.Default.Download,
                                contentDescription = "Exportálás",
                                onClick = createFile
                        )
                    }
                }
                if (importError) {
                    item {
                        Card(
                                modifier = Modifier
		                                .fillMaxWidth()
		                                .wrapContentHeight()
		                                .padding(8.dp),
                                colors = CardColors(
                                        containerColor = ExtendedTheme.colorScheme.warning.colorContainer,
                                        contentColor = ExtendedTheme.colorScheme.warning.onColorContainer,
                                        disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
                                        disabledContentColor = CardDefaults.cardColors().disabledContentColor
                                )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                        text = "Hiba történt az importálás során!",
                                        style = MaterialTheme.typography.titleLarge,
                                )
                                Text(
                                        text = "Ellenőrizd a fájl formátumát és próbáld újra.",
                                        style = MaterialTheme.typography.bodyMedium,
                                )
                            }

                        }
                    }
                }

                item {
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Infó",
                                        modifier = Modifier
		                                        .padding(8.dp)
		                                        .size(30.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Formátum",
                                        style = MaterialTheme.typography.titleLarge,
                                )
                            }
                            Text(
                                    text = "A fájl formátuma:\n" +
                                            "név1, kategória1, határidő1, leírás1, ikonUrl1, maxMancsok1\n" +
                                            "név2, kategória2, határidő2, leírás2, ikonUrl2, maxMancsok2\n...\n\n" +
                                            "Az összetartozó értékek vesszővel vannak elválasztva," +
                                            " a különböző projektek adatai pedig új sorban kezdődnek." +
                                            " (.csv formátum, a legtöbb táblázatkezelő programból exportálható).\n\n" +
                                            "A kategória mező értékei: Univerzális, Szervezetfejlesztés, Feladatsor," +
                                            " Média és DIY, IT, Pedagógia, Nyári tábori előkészítés, Évközi tábori előkészítés." +
                                            "Hibás érték esetén az importált projekt Univerzális lesz." +
                                            " A határidő mező értékei: ÉÉÉÉ-[H]H-[N]N\n" +
                                            "0-val kezdődő hó vagy nap esetén a 0 elhagyható.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            } else {
                item {
                    Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
		                            .fillMaxWidth()
		                            .padding(vertical = 8.dp)
                    ) {
                        AdminButton(
                                modifier = Modifier.weight(1f),
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Importálás",
                                onClick = selectFile
                        )
                        AdminButton(
                                modifier = Modifier.weight(1f),
                                imageVector = Icons.Default.Save,
                                contentDescription = "Mentés",
                                onClick = saveImport
                        )
                    }
                }
                items(importedProjects) { project ->
                    ImportProjectCard(project)
                }
            }
        }
    }
}
