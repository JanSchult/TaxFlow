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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taxflow.domain.usecase.OverviewMode
import com.example.taxflow.ui.components.OverviewFilterSheet
import com.example.taxflow.ui.components.PremiumGate
import com.example.taxflow.ui.navigation.Screen
import com.example.taxflow.util.formatCurrency
import com.example.taxflow.viewModel.OverviewViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = koinViewModel(),
    onNavigateToPaywall: () -> Unit,
    navController: NavController,
    onExportClick: () -> Unit,
    premiumViewModel: PremiumStatusViewModel = koinViewModel()
) {
    val isPremium by premiumViewModel.isPremium.collectAsState()
    val state by viewModel.uiState.collectAsState()
    var showFilter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    PremiumGate(
        isPremium = isPremium,
        title = "Übersicht",
        description = "Sieh auf einen Blick, wie viel du für Steuern zurücklegen solltest.",
        onUpgradeClick = onNavigateToPaywall
    ) {
        // Haupt-Column, um alle Elemente im PremiumGate sauber untereinander anzuordnen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Übersicht", style = MaterialTheme.typography.headlineMedium)
                BadgedBox(
                    badge = {
                        if (state.isFilterActive) Badge()
                    }
                ) {
                    IconButton(onClick = { showFilter = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                }
            }

            Row {
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
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.monthSummaries) { summary ->
                    Card(modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(
                                Screen.PeriodDetail.route(
                                    label = summary.label,
                                    from = summary.fromDate.toString(),
                                    to = summary.toDate.toString()
                                )
                            )
                        }
                        ) {
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

            OutlinedButton(
                onClick = onExportClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📄 Für Steuerberater exportieren")
            }
        }
    }
}