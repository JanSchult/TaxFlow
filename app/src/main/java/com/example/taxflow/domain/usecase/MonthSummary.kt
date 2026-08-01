package com.example.taxflow.domain.usecase

data class MonthSummary(
    val label: String,
    val income: Double,
    val expenses: Double,
    val profit: Double
)