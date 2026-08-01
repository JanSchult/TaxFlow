package com.example.shared2.domain.model

data class TaxReserveResult(
    val totalIncome: Double,
    val totalExpenses: Double,
    val profit: Double,
    val taxReserveAmount: Double,
    val availableAfterReserve: Double,
    val savingsGoalAmount: Double,
    val incomeItems: List<Transaction> = emptyList(),
    val expenseItems: List<Transaction> = emptyList()
)
