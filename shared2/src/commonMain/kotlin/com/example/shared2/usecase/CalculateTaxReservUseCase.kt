package com.example.shared2.usecase

import com.example.shared2.domain.model.TaxReserveResult
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.UserSettings


class CalculateTaxReserveUseCase {

    operator fun invoke(
        transactions: List<Transaction>,
        settings: UserSettings
    ): TaxReserveResult {
        // Hier trennen wir die Listen direkt auf
        val incomeItems = transactions.filter { it.type == TransactionType.INCOME }
        val expenseItems = transactions.filter { it.type == TransactionType.EXPENSE }

        // Summen bilden aus den gefilterten Listen
        val income = incomeItems.sumOf { it.amount }
        val expenses = expenseItems.sumOf { it.amount }

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
            savingsGoalAmount = savingsGoal,
            // Hier die Listen an das Ergebnis übergeben:
            incomeItems = incomeItems,
            expenseItems = expenseItems
        )
    }
}