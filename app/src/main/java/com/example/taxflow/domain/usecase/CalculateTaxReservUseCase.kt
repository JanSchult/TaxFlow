package com.example.taxflow.domain.usecase

import com.example.taxflow.domain.model.TaxReserveResult
import com.example.taxflow.domain.model.Transaction
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.domain.model.UserSettings

class CalculateTaxReserveUseCase {

    operator fun invoke(
        transactions: List<Transaction>,
        settings: UserSettings
    ): TaxReserveResult {
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val profit = (income - expenses).coerceAtLeast(0.0)

        val effectiveRate = (settings.taxRatePercent + settings.bufferPercent) / 100.0
        val taxReserve = profit * effectiveRate
        val savingsGoal = settings.monthlySavingsGoal
        val available = (profit - taxReserve - savingsGoal).coerceAtLeast(0.0)

        return TaxReserveResult(
            totalIncome = income,
            totalExpenses = expenses,
            profit = profit,
            taxReserveAmount = taxReserve,
            availableAfterReserve = available,
            savingsGoalAmount = savingsGoal
        )
    }
}