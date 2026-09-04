package com.example.shared2.domain.model

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Long = 0L,
    val grossAmount: Double,
    val netAmount: Double,
    val vatAmount: Double,
    val vatMode: VatMode,
    val type: TransactionType,
    val categoryId: Long,
    val categoryName: String = "",
    val date: LocalDate,
    val note: String = "",
    val taxDeductiblePercentage: Int = 100
) {
    /** Bequemlichkeits-Alias, wo bisher amount verwendet wurde */
    val amount: Double get() = grossAmount
}