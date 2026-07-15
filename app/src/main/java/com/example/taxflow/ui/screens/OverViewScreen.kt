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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taxflow.domain.model.OverviewMode
import com.example.taxflow.ui.components.PremiumGate
import com.example.taxflow.util.formatCurrency
import com.example.taxflow.viewModel.OverviewViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OverviewScreen(viewModel: OverviewViewModel = koinViewModel(),
                   onNavigateToPaywall: () -> Unit,
                   onExportClick: () -> Unit,
                   premiumViewModel: PremiumStatusViewModel = koinViewModel()) {

    val isPremium by premiumViewModel.isPremium.collectAsState()
    val state by viewModel.uiState.collectAsState()
    PremiumGate(
        isPremium = isPremium,
        title = "Übersicht",
        description = "Sieh auf einen Blick, wie viel du für Steuern zurücklegen solltest.",
        onUpgradeClick = onNavigateToPaywall
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Übersicht", style = MaterialTheme.typography.headlineMedium)

            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = state.mode == OverviewMode.MONTH,
                        onClick = { viewModel.setMode(OverviewMode.MONTH) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) { Text("Monate") }
                    SegmentedButton(
                        selected = state.mode == OverviewMode.YEAR,
                        onClick = { viewModel.setMode(OverviewMode.YEAR) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) { Text("Jahre") }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.monthSummaries) { summary ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(summary.label, style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Einnahmen: ${formatCurrency(summary.income, "EUR")}")
                                Text("Ausgaben: ${formatCurrency(summary.expenses, "EUR")}")
                            }
                            Text(
                                "Gewinn: ${formatCurrency(summary.profit, "EUR")}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (summary.profit >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            OutlinedButton(onClick = onExportClick, modifier = Modifier.fillMaxWidth()) {
                Text("📄 Für Steuerberater exportieren")
            }
        }
    }
}