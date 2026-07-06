package com.example.taxflow.domain.model

import java.time.LocalDate

data class TaxDeadline(
    val id: Long = 0L,
    val title: String,
    val dueDate: LocalDate,
    val note: String = "",
    val isPaid: Boolean = false,
)
