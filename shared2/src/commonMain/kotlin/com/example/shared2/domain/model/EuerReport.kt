package com.example.shared2.domain.model

data class EuerReport(
    val periodLabel: String,
    val vatMode: VatMode,

    // Einnahmen
    val incomeGross: Double,
    val incomeNet: Double,
    val incomeVat: Double,
    val incomeKleinunternehmer: Double,

    // Ausgaben
    val expensesByCategory: List<EuerCategoryLine>,
    val totalExpenses: Double,
    val totalDeductible: Double,
    val totalNonDeductible: Double,
    val expenseVat: Double,

    // Positionen
    val incomeItems: List<Transaction>,
    val expenseItems: List<Transaction>,

    // Ergebnis
    val taxableProfit: Double,
    val realProfit: Double
)
