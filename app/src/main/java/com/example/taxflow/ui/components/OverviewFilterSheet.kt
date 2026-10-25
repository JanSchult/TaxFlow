package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.viewModel.uiState.OverviewUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

@OptIn(ExperimentalLayoutApi::class)
@Composable
 fun OverviewFilterSheet(
    state: OverviewUiState,
    onToggleCategory: (String) -> Unit,
    onSetType: (TransactionType?) -> Unit,
    onSetDateRange: (LocalDate?, LocalDate?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter", style = MaterialTheme.typography.titleLarge)
            if (state.isFilterActive) {
                TextButton(onClick = onClearFilters) { Text("Zurücksetzen") }
            }
        }

        // --- Typ ---
        Text("Typ", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null to "Alle", TransactionType.INCOME to "Einnahmen", TransactionType.EXPENSE to "Ausgaben")
                .forEach { (type, label) ->
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { onSetType(type) },
                        label = { Text(label) }
                    )
                }
        }

        // --- Kategorien ---
        if (state.availableCategories.isNotEmpty()) {
            Text("Kategorien", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.availableCategories.forEach { cat ->
                    FilterChip(
                        selected = cat in state.selectedCategories,
                        onClick = { onToggleCategory(cat) },
                        label = { Text(cat) }
                    )
                }
            }
        } else {
            Text(
                "Keine Buchungen für diesen Typ vorhanden",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- Zeitraum ---
        Text("Zeitraum", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val today = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
            listOf(
                "Aktueller Monat" to Pair(
                    LocalDate(today.year, today.monthNumber, 1), today),
                "Letzter Monat" to Pair(
                    LocalDate(today.year, today.monthNumber, 1)
                        .minus(kotlinx.datetime.DatePeriod(months = 1)),
                    LocalDate(today.year, today.monthNumber, 1)
                        .minus(kotlinx.datetime.DatePeriod(days = 1))),
                "Dieses Jahr" to Pair(
                    LocalDate(today.year, 1, 1), LocalDate(today.year, 12, 31)),
                "Kein Zeitraum" to Pair(null, null)
            ).forEach { (label, range) ->
                val isSelected = state.customFrom == range.first && state.customTo == range.second
                FilterChip(
                    selected = isSelected,
                    onClick = { onSetDateRange(range.first, range.second) },
                    label = { Text(label) }
                )
            }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) { Text("Anwenden") }
    }
}