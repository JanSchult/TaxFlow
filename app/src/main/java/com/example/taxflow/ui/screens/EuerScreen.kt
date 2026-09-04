package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.EuerPeriodMode
import com.example.taxflow.ui.components.EuerCategoryCard
import com.example.taxflow.ui.components.EuerResultCard
import com.example.taxflow.ui.components.EuerSummaryCard
import com.example.taxflow.ui.components.TransactionDetailRow
import com.example.taxflow.viewModel.EuerViewModel
import kotlinx.datetime.todayIn
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EuerScreen(viewModel: EuerViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val today = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
    val monthNames = listOf("Jan","Feb","Mär","Apr","Mai","Jun",
        "Jul","Aug","Sep","Okt","Nov","Dez")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("EÜR-Übersicht", style = MaterialTheme.typography.headlineMedium)

        // Monat / Jahr Toggle
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = state.periodMode == EuerPeriodMode.MONTH,
                onClick = { viewModel.setPeriodMode(EuerPeriodMode.MONTH) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text("Monat") }
            SegmentedButton(
                selected = state.periodMode == EuerPeriodMode.YEAR,
                onClick = { viewModel.setPeriodMode(EuerPeriodMode.YEAR) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text("Jahr") }
        }

        // Zeitraum-Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Jahresauswahl
            var yearExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = yearExpanded,
                onExpandedChange = { yearExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = "${state.selectedYear}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jahr") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(yearExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                    (today.year downTo today.year - 4).forEach { year ->
                        DropdownMenuItem(
                            text = { Text("$year") },
                            onClick = { viewModel.setYear(year); yearExpanded = false }
                        )
                    }
                }
            }

            // Monatsauswahl (nur bei Monatsmodus)
            if (state.periodMode == EuerPeriodMode.MONTH) {
                var monthExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = monthExpanded,
                    onExpandedChange = { monthExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = monthNames[state.selectedMonth - 1],
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Monat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(monthExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                        monthNames.forEachIndexed { idx, name ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = { viewModel.setMonth(idx + 1); monthExpanded = false }
                            )
                        }
                    }
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            return@Column
        }

        val report = state.report ?: return@Column

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { EuerSummaryCard(report) }
            item {
                Text("Betriebseinnahmen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp))
            }
            items(report.incomeItems) { tx ->
                TransactionDetailRow(tx)
            }
            item {
                Text("Betriebsausgaben nach Kategorie",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp))
            }
            items(report.expensesByCategory) { line ->
                EuerCategoryCard(line)
            }
            item { EuerResultCard(report) }
        }
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun EuerScreenSimplePreview() {
    val monthNames = listOf("Jan","Feb","Mär","Apr","Mai","Jun","Jul","Aug","Sep","Okt","Nov","Dez")

    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("EÜR-Übersicht", style = MaterialTheme.typography.headlineMedium)

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = true,
                        onClick = {},
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text("Monat") }
                    SegmentedButton(
                        selected = false,
                        onClick = {},
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text("Jahr") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = "2026",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jahr") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "Aug",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Monat") },
                        modifier = Modifier.weight(1f)
                    )
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(
                            "Betriebseinnahmen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    item {
                        Text(
                            "Betriebsausgaben nach Kategorie",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}