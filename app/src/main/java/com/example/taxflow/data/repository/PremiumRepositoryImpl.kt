package com.example.taxflow.data.repository

import com.example.taxflow.billigmanager.BillingManager
import com.example.taxflow.data.settings.PremiumStatusDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PremiumRepositoryImpl(
    private val billingManager: BillingManager,
    private val dataStore: PremiumStatusDataStore
) : PremiumRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val isPremium: StateFlow<Boolean> = dataStore.isPremiumFlow
        .stateIn(scope, SharingStarted.Eagerly, false)

    init {
        // Live-Updates aus dem Kauf-Flow (z. B. direkt nach erfolgreichem Kauf) übernehmen.
        scope.launch {
            billingManager.purchaseUpdates.collect { purchases ->
                dataStore.setPremium(billingManager.containsActivePremium(purchases))
            }
        }
    }

    override suspend fun refreshFromPlayStore() {
        val connected = billingManager.connect()
        if (!connected) return
        val purchases = billingManager.queryActiveSubscriptionPurchases()
        dataStore.setPremium(billingManager.containsActivePremium(purchases))
    }
}