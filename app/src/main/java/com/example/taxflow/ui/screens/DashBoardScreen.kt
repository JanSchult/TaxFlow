package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taxflow.ui.components.PremiumGate
import com.example.taxflow.ui.components.SummaryRow
import com.example.taxflow.ui.components.TaxReserveCard
import com.example.taxflow.viewModel.DashboardViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel(),
                    onNavigateToPaywall: () -> Unit,
                    premiumViewModel: PremiumStatusViewModel = koinViewModel(),) {
    val state by viewModel.uiState.collectAsState()
    val isPremium by premiumViewModel.isPremium.collectAsState()

    PremiumGate(
        isPremium = isPremium,
        title = "Steuerrücklage & Dashboard",
        description = "Sieh auf einen Blick, wie viel du für Steuern zurücklegen solltest.",
        onUpgradeClick = onNavigateToPaywall
    ) {
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
            return@PremiumGate
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = state.currentMonthLabel,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item { TaxReserveCard(state.result, state.currencyCode) }
            item {
                SummaryRow(
                    label = "Einnahmen",
                    amount = state.result.totalIncome,
                    currency = state.currencyCode,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                SummaryRow(
                    label = "Ausgaben",
                    amount = state.result.totalExpenses,
                    currency = state.currencyCode,
                    color = MaterialTheme.colorScheme.error
                )
            }
            item {
                SummaryRow(
                    label = "Frei verfügbar (nach Rücklage & Sparziel)",
                    amount = state.result.availableAfterReserve,
                    currency = state.currencyCode,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}




