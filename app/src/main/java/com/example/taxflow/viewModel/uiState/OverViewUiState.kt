package com.example.taxflow.viewModel.uiState

import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.domain.usecase.MonthSummary
import com.example.taxflow.domain.usecase.OverviewMode
import kotlinx.datetime.LocalDate


data class OverviewUiState(
    val mode: OverviewMode = OverviewMode.MONTH,
    val monthSummaries: List<MonthSummary> = emptyList(),
    val isLoading: Boolean = true,
    val availableCategories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedType: TransactionType? = null,      // null = beide
    val customFrom: LocalDate? = null,
    val customTo: LocalDate? = null,
    val isFilterActive: Boolean = false
)