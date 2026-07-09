package com.example.taxflow.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.taxflow.viewModel.ReceiptScanViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.format.DateTimeFormatter

/**
 * Premium-Feature: Beleg fotografieren oder aus Galerie wählen -> ML Kit liest den Text
 * -> Betrag/Datum/Händler werden vorgeschlagen -> Nutzer prüft/korrigiert -> Übernahme
 * in die Erfassung (via ReceiptDraftHolder, siehe AddTransactionScreen-Integration).
 */
@Composable
fun ReceiptScanScreen(
    onDone: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ReceiptScanViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingPhotoUri?.let { uri ->
                readBitmap(context, uri)?.let { viewModel.processImage(it) }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { readBitmap(context, it)?.let { bmp -> viewModel.processImage(bmp) } }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            pendingPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = createImageUri(context)
            pendingPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Beleg scannen", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = "Abbrechen")
            }
        }

        if (state.scanResult == null) {
            Text(
                "Fotografiere einen Beleg oder wähle ein Foto aus der Galerie. " +
                        "Die Texterkennung läuft komplett auf deinem Gerät – nichts wird hochgeladen.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { launchCamera() }) { Text("Fotografieren") }
                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) { Text("Aus Galerie") }
            }

            if (state.isProcessing) {
                CircularProgressIndicator()
                Text("Beleg wird gelesen …", style = MaterialTheme.typography.bodyMedium)
            }

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        } else {
            val result = state.scanResult!!
            Text("Erkannt – bitte prüfen und ggf. korrigieren:", style = MaterialTheme.typography.titleMedium)

            var amountText by remember(result.amount) { mutableStateOf(result.amount?.toString().orEmpty()) }
            var vendorText by remember(result.vendorGuess) { mutableStateOf(result.vendorGuess.orEmpty()) }
            val dateLabel = result.date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "nicht erkannt"

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    viewModel.updateResult { current -> current.copy(amount = it.replace(",", ".").toDoubleOrNull()) }
                },
                label = { Text("Betrag") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vendorText,
                onValueChange = {
                    vendorText = it
                    viewModel.updateResult { current -> current.copy(vendorGuess = it) }
                },
                label = { Text("Händler / Notiz") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Erkanntes Datum: $dateLabel", style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    viewModel.confirmAndPublish()
                    onDone()
                }) { Text("Übernehmen") }
                OutlinedButton(onClick = { viewModel.reset() }) { Text("Erneut scannen") }
            }
        }
    }
}

private fun readBitmap(context: Context, uri: Uri) =
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

private fun createImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "receipt_scans").apply { mkdirs() }
    val file = File(imagesDir, "receipt_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}