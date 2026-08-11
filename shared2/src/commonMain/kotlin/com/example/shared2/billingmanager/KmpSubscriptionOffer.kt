package com.example.shared2.billingmanager

data class KmpSubscriptionOffer(
    val basePlanId: String,
    val offerToken: String,
    val formattedPrice: String
)