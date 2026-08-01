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
import com.example.shared2.domain.model.TaxDeadline
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun AddDeadlineDialog(
    initialDeadline: TaxDeadline? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, dueDate: LocalDate, note: String) -> Unit
) {
    // Heute berechnen
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    // Falls initialDeadline übergeben wird, berechnen wir die verbleibenden Tage dafür vor
    val initialDays = remember(initialDeadline) {
        initialDeadline?.dueDate?.let { today.daysUntil(it).toString() } ?: "30"
    }

    var title by remember { mutableStateOf(initialDeadline?.title ?: "") }
    var note by remember { mutableStateOf(initialDeadline?.note ?: "") }
    var daysFromNow by remember { mutableStateOf(initialDays) }

    val isEditing = initialDeadline != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Frist bearbeiten" else "Neue Frist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titel, z. B. USt-Voranmeldung") }
                )
                OutlinedTextField(
                    value = daysFromNow,
                    onValueChange = { daysFromNow = it },
                    label = { Text("Fällig in wie vielen Tagen?") }
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Notiz (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val days = daysFromNow.toIntOrNull() ?: 30
                if (title.isNotBlank()) {
                    // Datum berechnen mit kotlinx-datetime plus(..., DateTimeUnit.DAY)
                    val calculatedDate = today.plus(days, DateTimeUnit.DAY)
                    onConfirm(title, calculatedDate, note)
                }
            }) {
                Text(if (isEditing) "Speichern" else "Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}