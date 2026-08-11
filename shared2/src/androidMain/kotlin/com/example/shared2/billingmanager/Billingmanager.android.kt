package com.example.shared2.billingmanager

import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
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
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

actual class BillingManager(context: Context) {

    actual companion object {
        actual const val SUBSCRIPTION_PRODUCT_ID = "taxflow_premium"
        actual const val BASE_PLAN_MONTHLY = "monthly"
        actual const val BASE_PLAN_YEARLY = "yearly"
        private const val TAG = "BillingManager"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _purchaseUpdates = MutableSharedFlow<List<KmpPurchase>>(extraBufferCapacity = 1)
    actual val purchaseUpdates: SharedFlow<List<KmpPurchase>> = _purchaseUpdates.asSharedFlow()

    // Cacht die nativen Details für launchPurchaseFlow
    private var cachedProductDetails: ProductDetails? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch {
                purchases.forEach { acknowledgeIfNeeded(it) }
                _purchaseUpdates.emit(purchases.map { it.toKmpPurchase() })
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

    actual suspend fun connect(): Boolean = suspendCancellableCoroutine { cont ->
        if (billingClient.isReady) {
            cont.resume(true)
            return@suspendCancellableCoroutine
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                cont.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
            }

            override fun onBillingServiceDisconnected() {
                // ServiceReconnection erfolgt automatisch
            }
        })
    }

    actual suspend fun queryPremiumProductDetails(): KmpProductDetails? = suspendCancellableCoroutine { cont ->
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
                val details = result.productDetailsList.firstOrNull()
                cachedProductDetails = details
                cont.resume(details?.toKmpProductDetails())
            } else {
                Log.w(TAG, "queryProductDetailsAsync fehlgeschlagen: ${billingResult.debugMessage}")
                cont.resume(null)
            }
        }
    }

    actual fun launchPurchaseFlow(
        activity: PlatformActivity,
        details1: ProductDetails,
        offerToken: String
    ) {
        val details = cachedProductDetails ?: run {
            Log.e(TAG, "ProductDetails wurden noch nicht geladen.")
            return
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    actual suspend fun queryActiveSubscriptionPurchases(): List<KmpPurchase> = suspendCancellableCoroutine { cont ->
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(purchases.map { it.toKmpPurchase() })
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

    actual fun containsActivePremium(purchases: List<KmpPurchase>): Boolean =
        purchases.any { purchase ->
            purchase.products.contains(SUBSCRIPTION_PRODUCT_ID) &&
                    purchase.purchaseState == PurchaseState.PURCHASED
        }

    // --- Mapper-Funktionen ---

    private fun Purchase.toKmpPurchase(): KmpPurchase = KmpPurchase(
        orderId = orderId,
        products = products,
        purchaseState = when (purchaseState) {
            Purchase.PurchaseState.PURCHASED -> PurchaseState.PURCHASED
            Purchase.PurchaseState.PENDING -> PurchaseState.PENDING
            else -> PurchaseState.UNSPECIFIED
        },
        isAcknowledged = isAcknowledged,
        purchaseToken = purchaseToken
    )

    private fun ProductDetails.toKmpProductDetails(): KmpProductDetails {
        val offerList = subscriptionOfferDetails?.map { offer ->
            val pricePhase = offer.pricingPhases.pricingPhaseList.firstOrNull()
            KmpSubscriptionOffer(
                basePlanId = offer.basePlanId,
                offerToken = offer.offerToken,
                formattedPrice = pricePhase?.formattedPrice ?: ""
            )
        } ?: emptyList()

        return KmpProductDetails(
            productId = productId,
            offers = offerList
        )
    }
}