package com.example.taxflow.domain.model

data class MonthSummary(
    val label: String,
    val income: Double,
    val expenses: Double,
    val profit: Double
)
