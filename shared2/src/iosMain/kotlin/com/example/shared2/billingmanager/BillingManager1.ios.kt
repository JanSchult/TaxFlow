package com.example.shared2.billingmanager

import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual class BillingManager {

    actual companion object {
        actual const val SUBSCRIPTION_PRODUCT_ID = "taxflow_premium"
        actual const val BASE_PLAN_MONTHLY = "monthly"
        actual const val BASE_PLAN_YEARLY = "yearly"
    }

    private val _purchaseUpdates = MutableSharedFlow<List<KmpPurchase>>()
    actual val purchaseUpdates: SharedFlow<List<KmpPurchase>> = _purchaseUpdates.asSharedFlow()

    actual suspend fun connect(): Boolean = true

    actual suspend fun queryPremiumProductDetails(): KmpProductDetails? = null

    actual fun launchPurchaseFlow(
        activity: PlatformActivity,
        details1: com.android.billingclient.api.ProductDetails,
        offerToken: String
    ) {}

    actual suspend fun queryActiveSubscriptionPurchases(): List<KmpPurchase> = emptyList()

    actual fun containsActivePremium(purchases: List<KmpPurchase>): Boolean = false
}