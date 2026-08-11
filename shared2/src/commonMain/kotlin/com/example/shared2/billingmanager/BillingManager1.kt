package com.example.shared2.billingmanager

import kotlinx.coroutines.flow.SharedFlow

expect class BillingManager {
    val purchaseUpdates: SharedFlow<List<KmpPurchase>>
    suspend fun connect(): Boolean
    suspend fun queryPremiumProductDetails(): KmpProductDetails?
    fun launchPurchaseFlow(
        activity: PlatformActivity,
        details1: com.android.billingclient.api.ProductDetails,
        offerToken: String
    )
    suspend fun queryActiveSubscriptionPurchases(): List<KmpPurchase>
    fun containsActivePremium(purchases: List<KmpPurchase>): Boolean

    companion object {
         val SUBSCRIPTION_PRODUCT_ID: String
         val BASE_PLAN_MONTHLY: String
         val BASE_PLAN_YEARLY: String
    }
}