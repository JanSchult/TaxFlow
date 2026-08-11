package com.example.shared2.usecase

import com.example.shared2.domain.model.TaxReserveResult
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.domain.model.VatMode
import com.example.shared2.domain.model.VatResult


class CalculateTaxReserveUseCase {

    operator fun invoke(
        transactions: List<Transaction>,
        settings: UserSettings
    ): TaxReserveResult {

        // 1. Nach Typ trennen
        val incomeItems = transactions.filter { it.type == TransactionType.INCOME }
        val expenseItems = transactions.filter { it.type == TransactionType.EXPENSE }

        // 2. Ausgaben-Anteile berechnen
        //    taxDeductiblePercentage liegt direkt in Transaction (aus Kategorie übernommen)
        val totalExpenses = expenseItems.sumOf { it.amount }
        val taxDeductibleExpenses = expenseItems.sumOf { tx ->
            tx.amount * (tx.taxDeductiblePercentage / 100.0)
        }
        val nonDeductibleExpenses = totalExpenses - taxDeductibleExpenses

        // 3. Bruttoeinnahmen
        val grossIncome = incomeItems.sumOf { it.amount }

        // 4. USt-Berechnung (nur bei Regelbesteuerung)
        val vatResult: VatResult?
        val netIncome: Double

        if (settings.vatMode != VatMode.NONE && settings.vatMode.ratePercent != null) {
            val rate = settings.vatMode.ratePercent / 100.0
            netIncome = grossIncome / (1.0 + rate)
            val vatAmount = grossIncome - netIncome
            vatResult = VatResult(
                vatMode = settings.vatMode,
                grossIncome = grossIncome,
                netIncome = netIncome,
                vatAmount = vatAmount,
                vatRatePercent = settings.vatMode.ratePercent
            )
        } else {
            netIncome = grossIncome
            vatResult = null
        }

        // 5. Gewinne berechnen
        //    taxableProfit: für das Finanzamt (nur abziehbare Ausgaben)
        //    realProfit:    Cashflow (alle tatsächlichen Ausgaben)
        val taxableProfit = (netIncome - taxDeductibleExpenses).coerceAtLeast(0.0)
        val realProfit = (netIncome - totalExpenses).coerceAtLeast(0.0)

        // 6. Einkommensteuer-Rücklage auf steuerlichem Gewinn
        val effectiveRate = (settings.taxRatePercent + settings.bufferPercent) / 100.0
        val taxReserve = taxableProfit * effectiveRate
        val savingsGoal = settings.monthlySavingsGoal

        // 7. Frei verfügbar: vom echten Cashflow-Gewinn, nach Steuer-Rücklage
        val available = (realProfit - taxReserve - savingsGoal).coerceAtLeast(0.0)

        return TaxReserveResult(
            totalIncome = grossIncome,
            totalExpenses = totalExpenses,
            taxDeductibleExpenses = taxDeductibleExpenses,
            nonDeductibleExpenses = nonDeductibleExpenses,
            taxableProfit = taxableProfit,
            realProfit = realProfit,
            profit = taxableProfit,
            taxReserveAmount = taxReserve,
            availableAfterReserve = available,
            savingsGoalAmount = savingsGoal,
            incomeItems = incomeItems,
            expenseItems = expenseItems,
            vatResult = vatResult
        )
    }
}