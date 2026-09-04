package com.example.taxflow.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatEur(amount: Double): String = try {
    NumberFormat.getCurrencyInstance(Locale.GERMANY)
        .also { it.currency = Currency.getInstance("EUR") }
        .format(amount)
} catch (e: Exception) { "%.2f €".format(amount) }
