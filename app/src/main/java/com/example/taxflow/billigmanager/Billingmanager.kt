package com.example.taxflow.billigmanager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.android.billingclient.api.BillingClientStateListener

/**
 * Kapselt die komplette Google-Play-Billing-Kommunikation (Play Billing Library 9.x)
 * für EIN Abo-Produkt ("taxflow_premium") mit zwei Base Plans: "monthly" und "yearly".
 *
 * Wichtig: In Play Console muss ein Subscription-Produkt mit exakt der ID
 * [SUBSCRIPTION_PRODUCT_ID] angelegt werden, darin zwei aktive Base Plans mit den
 * IDs [BASE_PLAN_MONTHLY] und [BASE_PLAN_YEARLY] (siehe MONETIZATION.md).
 */
class BillingManager(context: Context) {

    companion object {
        const val SUBSCRIPTION_PRODUCT_ID = "taxflow_premium"
        const val BASE_PLAN_MONTHLY = "monthly"
        const val BASE_PLAN_YEARLY = "yearly"
        private const val TAG = "BillingManager"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _purchaseUpdates = MutableSharedFlow<List<Purchase>>(extraBufferCapacity = 1)
    /** Emittiert jede neue/aktualisierte Purchase-Liste, z. B. direkt nach einem Kauf. */
    val purchaseUpdates: SharedFlow<List<Purchase>> = _purchaseUpdates.asSharedFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch {
                purchases.forEach { acknowledgeIfNeeded(it) }
                _purchaseUpdates.emit(purchases)
            }
        } else {
            Log.w(
                TAG,
                "Purchase update mit Fehler: ${billingResult.responseCode} ${billingResult.debugMessage}"
            )
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    /** Baut die Verbindung zum Play-Store-Billing-Service auf. Vor jeder anderen Aktion aufrufen. */
    suspend fun connect(): Boolean = suspendCancellableCoroutine { cont ->
        if (billingClient.isReady) {
            cont.resume(true)
            return@suspendCancellableCoroutine
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                cont.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
            }

            override fun onBillingServiceDisconnected() {
                // enableAutoServiceReconnection() versucht automatisch erneut zu verbinden.
            }
        })
    }

    /** Lädt die ProductDetails für das Abo (inkl. beider Base Plans mit Preisen). */
    suspend fun queryPremiumProductDetails(): ProductDetails? = suspendCancellableCoroutine { cont ->
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(SUBSCRIPTION_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(result.productDetailsList.firstOrNull())
            } else {
                Log.w(TAG, "queryProductDetailsAsync fehlgeschlagen: ${billingResult.debugMessage}")
                cont.resume(null)
            }
        }
    }

    /** Startet den Kauf-Flow für einen bestimmten Base Plan (Offer Token aus ProductDetails). */
    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, offerToken: String) {
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    /**
     * Fragt beim Play Store nach aktuell aktiven Abo-Käufen (auch ohne dass der Nutzer
     * gerade etwas gekauft hat, z. B. beim App-Start oder für "Käufe wiederherstellen").
     */
    suspend fun queryActiveSubscriptionPurchases(): List<Purchase> = suspendCancellableCoroutine { cont ->
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(purchases)
            } else {
                cont.resume(emptyList())
            }
        }
    }

    private suspend fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            suspendCancellableCoroutine<Unit> { cont ->
                billingClient.acknowledgePurchase(params) { result ->
                    if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                        Log.w(TAG, "Acknowledge fehlgeschlagen: ${result.debugMessage}")
                    }
                    cont.resume(Unit)
                }
            }
        }
    }

    /** Ist unter den übergebenen Käufen ein aktives, gültiges Premium-Abo? */
    fun containsActivePremium(purchases: List<Purchase>): Boolean =
        purchases.any { purchase ->
            purchase.products.contains(SUBSCRIPTION_PRODUCT_ID) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
}