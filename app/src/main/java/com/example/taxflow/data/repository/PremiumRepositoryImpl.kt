package com.example.taxflow.data.repository

import com.example.shared2.data.PremiumRepository
import com.example.taxflow.billigmanager.BillingManager
import com.example.taxflow.data.settings.PremiumStatusDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val PAYWALL_TEMPORARILY_DISABLED = true
/** true = Premium aktiv, false = Free-Tier. Aus lokalem Cache, sofort verfügbar (auch offline). */

class PremiumRepositoryImpl(
    private val billingManager: BillingManager,
    private val dataStore: PremiumStatusDataStore
) : PremiumRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val isPremium: StateFlow<Boolean> = dataStore.isPremiumFlow
        .map{realStatus ->if (PAYWALL_TEMPORARILY_DISABLED) true else realStatus}
        .stateIn(scope, SharingStarted.Eagerly, PAYWALL_TEMPORARILY_DISABLED)

    init {
        // Live-Updates aus dem Kauf-Flow (z. B. direkt nach erfolgreichem Kauf) übernehmen.
        scope.launch {
            billingManager.purchaseUpdates.collect { purchases ->
                dataStore.setPremium(billingManager.containsActivePremium(purchases))
            }
        }
    }

    override suspend fun refreshFromPlayStore() {

        if (PAYWALL_TEMPORARILY_DISABLED) return

        val connected = billingManager.connect()
        if (!connected) return
        val purchases = billingManager.queryActiveSubscriptionPurchases()
        dataStore.setPremium(billingManager.containsActivePremium(purchases))
    }
}