package com.example.taxflow.domain.model

data class TaxReserveResult(
    val totalIncome: Double,
    val totalExpenses: Double,
    val profit: Double,
    val taxReserveAmount: Double,
    val availableAfterReserve: Double,
    val savingsGoalAmount: Double,
)
