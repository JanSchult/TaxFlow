package com.example.shared2.usecase

import com.example.shared2.domain.model.EuerCategoryLine
import com.example.shared2.domain.model.EuerReport
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.domain.model.VatMode

/**
 * Baut den strukturierten EÜR-Bericht aus einer Liste von Buchungen.
 * Orientiert sich an Anlage EÜR 2025 (Bundesfinanzministerium).
 *
 * USt-Berechnung: auf POSITIONSEBENE, nicht auf die Gesamtsumme.
 * Jede Transaktion hat ihren eigenen [netAmount]/[vatAmount] (berechnet beim Erfassen).
 *
 * Steuerlich abziehbare Ausgaben nach § 4 Abs. 5 EStG:
 * - Bewirtung: 70 % (§ 4 Abs. 5 Nr. 2 EStG)
 * - Bußgelder/Geldbußen: 0 % (§ 4 Abs. 5 Nr. 8 EStG)
 * - Alle anderen: gemäß taxDeductiblePercentage der Kategorie
 */
class BuildEuerReportUseCase {

    operator fun invoke(
        transactions: List<Transaction>,
        settings: UserSettings,
        periodLabel: String
    ): EuerReport {

        val incomeItems = transactions.filter { it.type == TransactionType.INCOME }
        val expenseItems = transactions.filter { it.type == TransactionType.EXPENSE }

        // === EINNAHMEN ===
        val isKleinunternehmer = settings.vatMode == VatMode.NONE

        // Zeile 12/13 EÜR: Kleinunternehmer → Gesamtbetrag ohne USt-Ausweis
        val incomeKleinunternehmer = if (isKleinunternehmer) {
            incomeItems.sumOf { it.grossAmount }
        } else 0.0

        // Zeile 14 EÜR: Regelbesteuerer → Nettobetrag (ohne USt)
        val incomeNet = if (!isKleinunternehmer) {
            incomeItems.sumOf { it.netAmount }
        } else 0.0

        // Zeile 16 EÜR: vereinnahmte USt (Summe der positionsweisen USt-Beträge)
        val incomeVat = incomeItems.sumOf { it.vatAmount }
        val incomeGross = incomeItems.sumOf { it.grossAmount }

        // === AUSGABEN (nach Kategorien gruppiert) ===
        val byCategory = expenseItems.groupBy { it.categoryName }

        val categoryLines = byCategory.map { (categoryName, txs) ->
            val deductiblePct = txs.firstOrNull()?.taxDeductiblePercentage ?: 100
            val gross = txs.sumOf { it.grossAmount }
            val deductible = txs.sumOf { it.netAmount * (deductiblePct / 100.0) }
            val nonDeductible = gross - deductible

            EuerCategoryLine(
                categoryName = categoryName,
                taxDeductiblePercentage = deductiblePct,
                grossAmount = gross,
                deductibleAmount = deductible,
                nonDeductibleAmount = nonDeductible,
                transactions = txs.sortedByDescending { it.date }
            )
        }.sortedBy { it.categoryName }

        val totalExpenses = expenseItems.sumOf { it.grossAmount }
        val totalDeductible = categoryLines.sumOf { it.deductibleAmount }
        val totalNonDeductible = categoryLines.sumOf { it.nonDeductibleAmount }

        // Zeile 59 EÜR: gezahlte Vorsteuer (aus Ausgaben-USt)
        val expenseVat = expenseItems.sumOf { it.vatAmount }

        // === GEWINN ===
        val incomeBase = if (isKleinunternehmer) incomeGross else incomeNet
        val taxableProfit = (incomeBase - totalDeductible).coerceAtLeast(0.0)
        val realProfit = (incomeBase - totalExpenses).coerceAtLeast(0.0)

        return EuerReport(
            periodLabel = periodLabel,
            vatMode = settings.vatMode,
            incomeGross = incomeGross,
            incomeNet = incomeNet,
            incomeVat = incomeVat,
            incomeKleinunternehmer = incomeKleinunternehmer,
            expensesByCategory = categoryLines,
            totalExpenses = totalExpenses,
            totalDeductible = totalDeductible,
            totalNonDeductible = totalNonDeductible,
            expenseVat = expenseVat,
            incomeItems = incomeItems.sortedByDescending { it.date },
            expenseItems = expenseItems.sortedByDescending { it.date },
            taxableProfit = taxableProfit,
            realProfit = realProfit
        )
    }
}