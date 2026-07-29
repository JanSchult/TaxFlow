package com.example.taxflow.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.taxflow.viewModel.BackupViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BackupScreen(viewModel: BackupViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val autoBackup by viewModel.autoBackupSettings.collectAsState()
    val context = LocalContext.current
    var showRestoreConfirm by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = state.exportedJson
        if (uri != null && json != null) {
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
        }
        viewModel.exportHandled()
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingRestoreUri = uri
            showRestoreConfirm = true
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            // Dauerhafte Schreibrechte sichern, sonst verliert die App den Zugriff
            // nach einem Geräte-Neustart.
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.onAutoBackupFolderSelected(uri.toString())
        }
    }

    LaunchedEffect(state.exportedJson) {
        if (state.exportedJson != null) {
            exportLauncher.launch("taxflow_backup_${LocalDate.now()}.json")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Backup & Wiederherstellung", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Alle Buchungen, Kategorien, Fristen und Einstellungen liegen nur lokal auf diesem " +
                    "Gerät. Erstelle regelmäßig eine Sicherung, damit bei Geräteverlust nichts verloren geht.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (state.isWorking) {
            CircularProgressIndicator()
        }

        state.message?.let {
            Text(
                it,
                color = if (state.isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.secondary
            )
        }

        Button(onClick = { viewModel.startExport() }, modifier = Modifier.fillMaxWidth()) {
            Text("Backup jetzt erstellen")
        }

        OutlinedButton(
            onClick = { importLauncher.launch(arrayOf("application/json")) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Backup wiederherstellen")
        }

        HorizontalDivider()

        Text("Automatisches Backup", style = MaterialTheme.typography.titleMedium)
        Text(
            "Legt einmal pro Woche automatisch eine neue Sicherung im gewählten Ordner ab " +
                    "(die letzten 5 automatischen Backups bleiben erhalten, ältere werden gelöscht).",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Automatisch sichern", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = autoBackup.isEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && autoBackup.folderUri.isNullOrBlank()) {
                        folderPickerLauncher.launch(null)
                    } else {
                        viewModel.onAutoBackupToggled(enabled)
                    }
                }
            )
        }

        OutlinedButton(
            onClick = { folderPickerLauncher.launch(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (autoBackup.folderUri.isNullOrBlank()) "Ordner auswählen" else "Ordner ändern")
        }

        val lastBackupLabel = autoBackup.lastBackupAt?.let {
            try {
                LocalDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
            } catch (e: Exception) {
                null
            }
        }
        Text(
            "Letztes automatisches Backup: ${lastBackupLabel ?: "noch keins"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showRestoreConfirm && pendingRestoreUri != null) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = { Text("Wirklich wiederherstellen?") },
            text = {
                Text(
                    "Das überschreibt ALLE aktuellen Buchungen, Kategorien und Fristen " +
                            "unwiderruflich mit dem Inhalt der Backup-Datei. Dieser Schritt kann " +
                            "nicht rückgängig gemacht werden."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val uri = pendingRestoreUri
                    val json = uri?.let {
                        context.contentResolver.openInputStream(it)?.use { stream ->
                            BufferedReader(InputStreamReader(stream)).readText()
                        }
                    }
                    if (json != null) viewModel.restore(json)
                    showRestoreConfirm = false
                }) { Text("Überschreiben") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false }) { Text("Abbrechen") }
            }
        )
    }
}