package com.example.taxflow.viewModel.uiState

import com.example.taxflow.viewModel.SubscriptionPlan

data class PaywallUiState(
    val isLoading: Boolean = true,
    val monthlyPlan: SubscriptionPlan? = null,
    val yearlyPlan: SubscriptionPlan? = null,
    val errorMessage: String? = null,
    val purchaseSuccessful: Boolean = false
)