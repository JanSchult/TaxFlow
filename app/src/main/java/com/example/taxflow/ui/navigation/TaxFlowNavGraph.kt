package com.example.taxflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taxflow.ui.screens.AddTransactionScreen
import com.example.taxflow.ui.screens.BackupScreen
import com.example.taxflow.ui.screens.DashboardScreen
import com.example.taxflow.ui.screens.DeadlinesScreen
import com.example.taxflow.ui.screens.ExportScreen
import com.example.taxflow.ui.screens.OverviewScreen
import com.example.taxflow.ui.screens.PaywallScreen
import com.example.taxflow.ui.screens.PeriodDetailScreen
import com.example.taxflow.ui.screens.ReceiptScanScreen
import com.example.taxflow.ui.screens.SettingsScreen
import com.example.taxflow.viewModel.PremiumStatusViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaxFlowNavGraph(bottomItems: List<BottomItem> = emptyList()) {
    val navController = rememberNavController()

    val premiumStatusViewModel: PremiumStatusViewModel = koinViewModel()
    val isPremium by premiumStatusViewModel.isPremium.collectAsState()

    val goToReceiptScanOrPaywall: () -> Unit = {
        if (isPremium) navController.navigate(Screen.ReceiptScan)
        else navController.navigate(Screen.Paywall)
    }

    Scaffold(
        bottomBar = {
            if (bottomItems.isNotEmpty()) {
                NavigationBar {
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = backStackEntry?.destination

                    bottomItems.forEach { item ->
                        // Typsichere Überprüfung via hasRoute
                        val selected = currentDestination?.hasRoute(item.screen::class) == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Dashboard> {
                DashboardScreen(onNavigateToPaywall = { navController.navigate(Screen.Paywall) })
            }

            composable<Screen.AddTransaction> {
                AddTransactionScreen(
                    onSaved = { navController.navigate(Screen.Dashboard) },
                    onScanReceiptClick = goToReceiptScanOrPaywall
                )
            }

            composable<Screen.Overview> {
                OverviewScreen(
                    onNavigateToPaywall = { navController.navigate(Screen.Paywall) },
                    onExportClick = { navController.navigate(Screen.Export) },
                    navController = navController
                )
            }

            composable<Screen.Deadlines> {
                DeadlinesScreen(onNavigateToPaywall = { navController.navigate(Screen.Paywall) })
            }

            composable<Screen.Settings> {
                SettingsScreen(onNavigateToBackup = { navController.navigate(Screen.Backup) })
            }

            composable<Screen.ReceiptScan> {
                ReceiptScanScreen(
                    onDone = {
                        navController.navigate(Screen.AddTransaction) {
                            popUpTo(Screen.AddTransaction) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable<Screen.Paywall> {
                PaywallScreen(
                    onDismiss = { navController.popBackStack() },
                    onPurchaseSuccessful = { navController.popBackStack() }
                )
            }

            composable<Screen.Export> {
                ExportScreen()
            }

            composable<Screen.Backup> {
                BackupScreen()
            }

            // Typsichere Navigation mit Parametern
            composable<Screen.PeriodDetail> { backStackEntry ->
                val detailRoute = backStackEntry.toRoute<Screen.PeriodDetail>()
                PeriodDetailScreen(
                    label = detailRoute.label,
                    from = detailRoute.from,
                    to = detailRoute.to,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}