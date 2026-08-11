package com.example.taxflow.viewModel.uiState

import com.example.shared2.domain.model.TaxReserveResult
import com.example.shared2.domain.model.VatResult


data class DashboardUiState(
    val currentMonthLabel: String = "",
    val result: TaxReserveResult = TaxReserveResult(
        totalIncome = 0.0,
        totalExpenses = 0.0,
        taxDeductibleExpenses = 0.0,
        nonDeductibleExpenses = 0.0,
        taxableProfit = 0.0,
        realProfit = 0.0,
        profit = 0.0,
        taxReserveAmount = 0.0,
        availableAfterReserve = 0.0,
        savingsGoalAmount = 0.0,
        incomeItems = emptyList(),
        expenseItems = emptyList(),
        vatResult = null
    ),
    val currencyCode: String = "EUR",
    val isLoading: Boolean = true,
    val taxRatePercent: Double = 0.0,
    val bufferPercent: Double = 0.0
)