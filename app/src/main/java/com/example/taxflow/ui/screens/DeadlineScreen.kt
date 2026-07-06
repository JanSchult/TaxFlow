package com.example.taxflow.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taxflow.ui.components.AddDeadlineDialog
import com.example.taxflow.viewModel.DeadlinesViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun DeadlinesScreen(viewModel: DeadlinesViewModel = koinViewModel()) {
    val deadlines by viewModel.deadlines.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(deadline.title, style = MaterialTheme.typography.titleMedium)
                                Text("Fällig am ${deadline.dueDate.format(formatter)}")
                                if (deadline.note.isNotBlank()) Text(deadline.note, style = MaterialTheme.typography.bodyMedium)
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

    if (showDialog) {
        AddDeadlineDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, date, note ->
                viewModel.addDeadline(title, date, note)
                showDialog = false
            }
        )
    }
}

