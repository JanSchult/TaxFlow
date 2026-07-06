package com.example.taxflow.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val date: LocalDate,
    val note: String = ""
)