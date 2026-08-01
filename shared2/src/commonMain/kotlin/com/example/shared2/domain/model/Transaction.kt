package com.example.shared2.domain.model

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val date: LocalDate,
    val note: String = ""
)