package com.example.taxflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taxflow.ui.screens.AddTransactionScreen
import com.example.taxflow.ui.screens.DashboardScreen
import com.example.taxflow.ui.screens.DeadlinesScreen
import com.example.taxflow.ui.screens.OverviewScreen
import com.example.taxflow.ui.screens.PaywallScreen
import com.example.taxflow.ui.screens.SettingsScreen

@Composable
fun TaxFlowNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                bottomItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.screen.label) },
                        label = { androidx.compose.material3.Text(item.screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }) }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(onSaved = { navController.navigate(Screen.Dashboard.route) })
            }
            composable(Screen.Overview.route) { OverviewScreen(onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }) }
            composable(Screen.Deadlines.route) { DeadlinesScreen(onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }) }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onDismiss = { navController.popBackStack() },
                    onPurchaseSuccessful = { navController.popBackStack() }
                )
            }
        }
    }
}