package com.example.taxflow.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.taxflow.domain.model.ExportMode
import com.example.taxflow.domain.model.ExportPeriodType
import com.example.taxflow.viewModel.ExportViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File

/**
 * Premium-Feature: PDF-Export für den Steuerberater.
 * "Sammelbericht" = ein PDF für den ganzen Zeitraum, itemisiert + Summen.
 * "Einzelne Belege" = ein PDF pro Buchung, gemeinsam über den Share-Dialog verschickbar.
 */
@Composable
fun ExportScreen(viewModel: ExportViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.generatedFiles) {
        val files = state.generatedFiles
        if (!files.isNullOrEmpty()) {
            shareFiles(context, files)
            viewModel.clearResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Export für Steuerberater", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Exportiere deine Buchungen als PDF – als ein gesammelter Bericht oder als einzelne Belege pro Buchung.",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Zeitraum", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = state.periodType == ExportPeriodType.MONTH,
                onClick = { viewModel.setPeriodType(ExportPeriodType.MONTH) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Aktueller Monat") }
            SegmentedButton(
                selected = state.periodType == ExportPeriodType.YEAR,
                onClick = { viewModel.setPeriodType(ExportPeriodType.YEAR) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Aktuelles Jahr") }
        }

        Text("Format", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = state.mode == ExportMode.SUMMARY,
                onClick = { viewModel.setMode(ExportMode.SUMMARY) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Sammelbericht") }
            SegmentedButton(
                selected = state.mode == ExportMode.INDIVIDUAL,
                onClick = { viewModel.setMode(ExportMode.INDIVIDUAL) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Einzelne Belege") }
        }

        if (state.isGenerating) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = { viewModel.generate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Als PDF exportieren")
        }
    }
}

private fun shareFiles(context: Context, files: List<File>) {
    val uris = files.map { file ->
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val intent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uris.first())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "application/pdf"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    context.startActivity(Intent.createChooser(intent, "PDF exportieren"))
}