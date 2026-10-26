package com.example.taxflow.ui.navigation

import Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomItem(val screen: Screen,val label : String, val icon: ImageVector)

val bottomItems = listOf(
    BottomItem(Screen.Dashboard, "Übersicht", Icons.Filled.Home),
    BottomItem(Screen.AddTransaction, "Erfassen", Icons.Filled.Add),
    // BottomItem(Screen.Euer, "Euer", Icons.Default.Assessment),
    BottomItem(Screen.Overview, "Monat/Jahr", Icons.Filled.DateRange),
    BottomItem(Screen.Deadlines, "Fristen", Icons.Filled.Notifications),
    BottomItem(Screen.Settings, "Einstellungen", Icons.Filled.Settings)
)