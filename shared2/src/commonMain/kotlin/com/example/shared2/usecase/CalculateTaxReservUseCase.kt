package com.example.shared2.usecase

import com.example.shared2.domain.model.TaxReserveResult
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.domain.model.VatMode
import com.example.shared2.domain.model.VatResult


class CalculateTaxReserveUseCase(
    private val buildEuerReportUseCase: BuildEuerReportUseCase = BuildEuerReportUseCase()
) {

    operator fun invoke(
        transactions: List<Transaction>,
        settings: UserSettings,
        periodLabel: String = ""
    ): TaxReserveResult {

        // 1. EÜR-Bericht generieren (übernimmt positionsweise Netto/Brutto/USt & Absetzbarkeit)
        val euerReport = buildEuerReportUseCase(transactions, settings, periodLabel)

        // 2. USt-Ergebnis aggregieren
        val vatResult = if (settings.vatMode != VatMode.NONE) {
            VatResult(
                vatMode = settings.vatMode,
                grossIncome = euerReport.incomeGross,
                netIncome = euerReport.incomeNet,
                vatAmount = euerReport.incomeVat,
                vatRatePercent = settings.vatMode.ratePercent ?: 0.0
            )
        } else {
            null
        }

        // 3. Gewinne aus dem EÜR-Bericht übernehmen
        val taxableProfit = euerReport.taxableProfit
        val realProfit = euerReport.realProfit

        // 4. Einkommensteuer-Rücklage berechnen (auf steuerlichem Gewinn)
        val effectiveRate = (settings.taxRatePercent + settings.bufferPercent) / 100.0
        val taxReserve = taxableProfit * effectiveRate
        val savingsGoal = settings.monthlySavingsGoal

        // 5. Frei verfügbar (Cashflow-Gewinn abzüglich Steuerrücklage und Sparziel)
        val available = (realProfit - taxReserve - savingsGoal).coerceAtLeast(0.0)

        return TaxReserveResult(
            totalIncome = euerReport.incomeGross,
            totalExpenses = euerReport.totalExpenses,
            taxDeductibleExpenses = euerReport.totalDeductible,
            nonDeductibleExpenses = euerReport.totalNonDeductible,
            taxableProfit = taxableProfit,
            realProfit = realProfit,
            profit = taxableProfit,
            taxReserveAmount = taxReserve,
            availableAfterReserve = available,
            savingsGoalAmount = savingsGoal,
            incomeItems = euerReport.incomeItems,
            expenseItems = euerReport.expenseItems,
            vatResult = vatResult
        )
    }
}