package com.example.shared2.domain.model

import kotlinx.datetime.LocalDate

data class TaxDeadline(
    val id: Long = 0L,
    val title: String,
    val dueDate: LocalDate,
    val note: String = "",
    val isPaid: Boolean = false,
)
