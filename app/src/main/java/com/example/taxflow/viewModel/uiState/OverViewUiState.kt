package com.example.taxflow.viewModel.uiState

import com.example.taxflow.domain.usecase.MonthSummary
import com.example.taxflow.domain.usecase.OverviewMode


data class OverviewUiState(
    val mode: OverviewMode = OverviewMode.MONTH,
    val monthSummaries: List<MonthSummary> = emptyList(),
    val isLoading: Boolean = true
)