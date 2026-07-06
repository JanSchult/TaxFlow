package com.example.taxflow.domain.model

data class UserSettings(
    val taxRatePercent: Double,
    val currencyCode: String = "Eur",
    val monthlySavingsGoal: Double = 0.0,
    val bufferPercent : Double = 0.0
)
