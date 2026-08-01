package com.example.taxflow.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TaxDeadline
import com.example.taxflow.ui.components.AddDeadlineDialog
import com.example.taxflow.ui.components.PremiumGate
import com.example.taxflow.viewModel.DeadlinesViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeadlinesScreen(
    viewModel: DeadlinesViewModel = koinViewModel(),
    premiumViewModel: PremiumStatusViewModel = koinViewModel(),
    onNavigateToPaywall: () -> Unit
) {
    val isPremium by premiumViewModel.isPremium.collectAsState()
    val deadlines by viewModel.deadlines.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var deadlineToEdit by remember { mutableStateOf<TaxDeadline?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()){}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    PremiumGate(
        isPremium = isPremium,
        title = "Fristen & Benachrichtigungen",
        description = "Füge Fristen hinzu und beachte die Benachrichtigungen.",
        onUpgradeClick = onNavigateToPaywall
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Frist hinzufügen")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Fristen & Erinnerungen", style = MaterialTheme.typography.headlineMedium)

                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(deadlines) { deadline ->
                        // Datum manuell für kotlinx-datetime formatieren
                        val formattedDate = remember(deadline.dueDate) {
                            val day = deadline.dueDate.dayOfMonth.toString().padStart(2, '0')
                            val month = deadline.dueDate.monthNumber.toString().padStart(2, '0')
                            val year = deadline.dueDate.year
                            "$day.$month.$year"
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        deadline.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Fällig am $formattedDate")
                                    if (deadline.note.isNotBlank()) {
                                        Text(
                                            deadline.note,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                // Interaktions-Buttons
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { deadlineToEdit = deadline }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Bearbeiten")
                                    }
                                    IconButton(onClick = { viewModel.delete(deadline) }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Löschen",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Checkbox(
                                        checked = deadline.isPaid,
                                        onCheckedChange = { viewModel.togglePaid(deadline) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog für neue Frist
        if (showAddDialog) {
            AddDeadlineDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, date, note ->
                    viewModel.addDeadline(title, date, note)
                    showAddDialog = false
                }
            )
        }

        // Dialog für das Bearbeiten einer bestehenden Frist
        deadlineToEdit?.let { currentDeadline ->
            AddDeadlineDialog(
                initialDeadline = currentDeadline, // Übergibt die zu bearbeitende Frist
                onDismiss = { deadlineToEdit = null },
                onConfirm = { title, date, note ->
                    val updatedDeadline = currentDeadline.copy(
                        title = title,
                        dueDate = date,
                        note = note
                    )
                    viewModel.update(updatedDeadline)
                    deadlineToEdit = null
                }
            )
        }
    }
}