package com.example.shared2.domain.model

data class TaxReserveResult(
    val totalIncome: Double,
    val totalExpenses: Double,
    val taxDeductibleExpenses: Double,
    val nonDeductibleExpenses: Double,
    val taxableProfit: Double,
    val realProfit: Double,
    val profit: Double,               // = taxableProfit (Basis für Rücklage)
    val taxReserveAmount: Double,
    val availableAfterReserve: Double,
    val savingsGoalAmount: Double,
    val incomeItems: List<Transaction> = emptyList(),
    val expenseItems: List<Transaction> = emptyList(),
    val vatResult: VatResult? = null
)
