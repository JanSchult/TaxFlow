package com.example.shared2.billingmanager

data class KmpProductDetails(
    val productId: String,
    val offers: List<KmpSubscriptionOffer>
)