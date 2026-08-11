package com.example.shared2.domain.model

data class Category(
    val id: Long = 0L,
    val name: String,
    val type: TransactionType,
    val colorHex: String = "#4C6EF5",
    val isDefault: Boolean = false,
    val taxDeductiblePercentage: Int,
    val supportsMileageCalculator: Boolean = false
)