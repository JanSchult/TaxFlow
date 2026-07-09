package com.example.taxflow.domain.model

import java.time.LocalDate

data class ReceiptScanData(
    val amount: Double?,
    val date: LocalDate?,
    val vendorGuess: String?,
    val rawText: String
)