package com.example.taxflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings

data class BottomItem(val screen: Screen, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomItems = listOf(
    BottomItem(Screen.Dashboard, Icons.Filled.Home),
    BottomItem(Screen.AddTransaction, Icons.Filled.Add),
    BottomItem(Screen.Euer, Icons.Default.Assessment),
    BottomItem(Screen.Overview, Icons.Filled.DateRange),
    BottomItem(Screen.Deadlines, Icons.Filled.Notifications),
    BottomItem(Screen.Settings, Icons.Filled.Settings)
)
