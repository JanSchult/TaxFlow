package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun AddDeadlineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, LocalDate, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    // Für die MVP-Einfachheit: Fälligkeitsdatum als Tage ab heute erfassen.
    var daysFromNow by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neue Frist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titel, z. B. USt-Voranmeldung") })
                OutlinedTextField(value = daysFromNow, onValueChange = { daysFromNow = it }, label = { Text("Fällig in wie vielen Tagen?") })
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Notiz (optional)") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val days = daysFromNow.toLongOrNull() ?: 30L
                if (title.isNotBlank()) {
                    onConfirm(title, LocalDate.now().plusDays(days), note)
                }
            }) { Text("Speichern") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}