package com.example.shared2.billingmanager

data class KmpPurchase(
    val orderId: String?,
    val products: List<String>,
    val purchaseState: PurchaseState,
    val isAcknowledged: Boolean,
    val purchaseToken: String
)