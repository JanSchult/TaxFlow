package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.ui.components.TransactionRow
import com.example.taxflow.util.formatCurrency
import com.example.taxflow.viewModel.PeriodDetailViewModel
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PeriodDetailScreen(
    label: String,
    from: String,
    to: String,
    onBack: () -> Unit,
    viewModel: PeriodDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(label, from, to) {
        viewModel.init(
            label = label,
            from = LocalDate.parse(from),
            to = LocalDate.parse(to)
        )
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    BadgedBox(badge = {
                        if (state.selectedCategories.isNotEmpty() || state.selectedType != null)
                            Badge()
                    }) {
                        IconButton(onClick = { viewModel.toggleFilter() }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // --- Zusammenfassung ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Zusammenfassung", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Einnahmen")
                            Text(
                                formatCurrency(state.totalIncome, "EUR"),
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ausgaben")
                            Text(
                                formatCurrency(state.totalExpenses, "EUR"),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gewinn", fontWeight = FontWeight.Bold)
                            val profit = state.totalIncome - state.totalExpenses
                            Text(
                                formatCurrency(profit, "EUR"),
                                fontWeight = FontWeight.Bold,
                                color = if (profit >= 0) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                        if (state.totalVat > 0.01) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("davon USt-Rücklage", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    formatCurrency(state.totalVat, "EUR"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // --- Buchungsliste ---
            if (state.filteredTransactions.isEmpty()) {
                item {
                    Text(
                        "Keine Buchungen für diesen Filter.",
                        modifier = Modifier.padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Einnahmen
                val income = state.filteredTransactions.filter {
                    it.type == TransactionType.INCOME
                }
                if (income.isNotEmpty()) {
                    item {
                        Text(
                            "Einnahmen",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(income) { tx ->
                        TransactionRow(tx)
                    }
                }

                // Ausgaben
                val expenses = state.filteredTransactions.filter {
                    it.type == TransactionType.EXPENSE
                }
                if (expenses.isNotEmpty()) {
                    item {
                        Text(
                            "Ausgaben",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(expenses) { tx ->
                        TransactionRow(tx)
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (state.showFilter) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleFilter() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter", style = MaterialTheme.typography.titleLarge)
                    if (state.selectedCategories.isNotEmpty() || state.selectedType != null) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Zurücksetzen")
                        }
                    }
                }

                // Typ
                Text("Typ", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        null to "Alle",
                        TransactionType.INCOME to "Einnahmen",
                        TransactionType.EXPENSE to "Ausgaben"
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = state.selectedType == type,
                            onClick = { viewModel.setType(type) },
                            label = { Text(label) }
                        )
                    }
                }

                // Einnahmen-Kategorien
                if (state.incomeCategories.isNotEmpty()) {
                    Text("Einnahmen-Kategorien", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.incomeCategories.forEach { cat ->
                            FilterChip(
                                selected = cat in state.selectedCategories,
                                onClick = { viewModel.toggleCategory(cat) },
                                label = { Text(cat) }
                            )
                        }
                    }
                }

                // Ausgaben-Kategorien
                if (state.expenseCategories.isNotEmpty()) {
                    Text("Ausgaben-Kategorien", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.expenseCategories.forEach { cat ->
                            FilterChip(
                                selected = cat in state.selectedCategories,
                                onClick = { viewModel.toggleCategory(cat) },
                                label = { Text(cat) }
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.toggleFilter() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) { Text("Anwenden") }
            }
        }
    }
}