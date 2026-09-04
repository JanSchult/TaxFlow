package com.example.taxflow.ui.screens

import ExpandableSummaryCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taxflow.ui.components.LockedOverlay
import com.example.taxflow.ui.components.SummaryRow
import com.example.taxflow.ui.components.TaxBreakdownCard
import com.example.taxflow.ui.components.VatCard
import com.example.taxflow.viewModel.DashboardViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToPaywall: () -> Unit,
    premiumViewModel: PremiumStatusViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val isPremium by premiumViewModel.isPremium.collectAsState()

    // Zustände, ob die jeweiligen Sektionen ausgefahren sind
    var isIncomeExpanded by remember { mutableStateOf(false) }
    var isExpensesExpanded by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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

            // --- SECTION: UMSATZSTEUER ---
            item {
                LockedOverlay(
                    isLocked = !isPremium,
                    label = "Umsatzsteuer-Übersicht freischalten",
                    onUnlockClick = onNavigateToPaywall,
                ) {
                    VatCard(
                        vatResult = state.result.vatResult,
                        currencyCode = state.currencyCode
                    )
                }
            }

            // --- SECTION: STEUERRÜCKLAGE & GEWINN ---
            item {
                LockedOverlay(
                    isLocked = !isPremium,
                    label = "Steuerrücklage freischalten",
                    onUnlockClick = onNavigateToPaywall,
                ) {
                    TaxBreakdownCard(
                        result = state.result,
                        currencyCode = state.currencyCode,
                        taxRatePercent = state.taxRatePercent,
                        bufferPercent = state.bufferPercent
                    )
                }
            }

            // --- SECTION: EINNAHMEN (EXPANDABLE) ---
            item {
                ExpandableSummaryCard(
                    label = "Einnahmen",
                    totalAmount = state.result.totalIncome,
                    currency = state.currencyCode,
                    color = MaterialTheme.colorScheme.secondary,
                    isExpanded = isIncomeExpanded,
                    onExpandToggle = { isIncomeExpanded = !isIncomeExpanded },
                    items = state.result.incomeItems
                )
            }

            // --- SECTION: AUSGABEN (EXPANDABLE) ---
            item {
                ExpandableSummaryCard(
                    label = "Ausgaben (Brutto)",
                    totalAmount = state.result.totalExpenses,
                    currency = state.currencyCode,
                    color = MaterialTheme.colorScheme.error,
                    isExpanded = isExpensesExpanded,
                    onExpandToggle = { isExpensesExpanded = !isExpensesExpanded },
                    items = state.result.expenseItems
                )
            }

            // --- SECTION: SPARZIEL ---
            if (state.result.savingsGoalAmount > 0) {
                item {
                    SummaryRow(
                        label = "Sparziel",
                        amount = state.result.savingsGoalAmount,
                        currency = state.currencyCode,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // --- SECTION: FREI VERFÜGBAR ---
            item {
                LockedOverlay(
                    isLocked = !isPremium,
                    label = "Frei verfügbar (nach Rücklage & Sparziel) freischalten",
                    onUnlockClick = onNavigateToPaywall
                ) {
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
}