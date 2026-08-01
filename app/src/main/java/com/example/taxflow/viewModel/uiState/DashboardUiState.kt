package com.example.taxflow.viewModel.uiState

import com.example.shared2.domain.model.TaxReserveResult


data class DashboardUiState(
    val currentMonthLabel: String = "",
    val result: TaxReserveResult = TaxReserveResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val currencyCode: String = "EUR",
    val isLoading: Boolean = true
)