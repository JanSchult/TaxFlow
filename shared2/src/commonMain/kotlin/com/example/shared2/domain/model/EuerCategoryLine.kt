package com.example.shared2.domain.model

data class EuerCategoryLine(
    val categoryName: String,
    val taxDeductiblePercentage: Int,
    val grossAmount: Double,
    val deductibleAmount: Double,
    val nonDeductibleAmount: Double,
    val transactions: List<Transaction>
)