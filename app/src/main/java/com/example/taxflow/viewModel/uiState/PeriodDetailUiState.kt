package com.example.taxflow.viewModel.uiState

import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType

data class PeriodDetailUiState(
    val label: String = "",
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val incomeCategories: List<String> = emptyList(),
    val expenseCategories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedType: TransactionType? = null,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalVat: Double = 0.0,
    val isLoading: Boolean = true,
    val showFilter: Boolean = false
)