package com.example.taxflow.domain.usecase

import kotlinx.datetime.LocalDate

data class MonthSummary(
    val label: String,
    val income: Double,
    val expenses: Double,
    val profit: Double,
    val fromDate: LocalDate,
    val toDate: LocalDate
)