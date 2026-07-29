package com.example.taxflow.ui.navigation

sealed class Screen(val route: String, val label: String) {
    data object Dashboard : Screen("dashboard", "Übersicht")
    data object AddTransaction : Screen("add_transaction", "Erfassen")
    data object Overview : Screen("overview", "Monat/Jahr")
    data object Deadlines : Screen("deadlines", "Fristen")
    data object Settings : Screen("settings", "Einstellungen")
    data object Paywall : Screen("paywall", "Premium")
    data object ReceiptScan : Screen("receipt_scan", "Beleg scannen")
    data object Export : Screen("export", "Export")
    data object Backup : Screen("backup", "Backup")
}