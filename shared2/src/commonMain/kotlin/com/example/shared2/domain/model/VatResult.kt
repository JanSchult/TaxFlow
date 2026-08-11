package com.example.shared2.domain.model

data class VatResult(
    val vatMode: VatMode,
    val grossIncome: Double,       // Bruttoeinnahmen (wie erfasst)
    val netIncome: Double,         // Nettoeinnahmen = grossIncome / (1 + rate)
    val vatAmount: Double,         // Zu überweisender USt-Betrag ans FA
    val vatRatePercent: Double     // z. B. 19.0 oder 7.0
)