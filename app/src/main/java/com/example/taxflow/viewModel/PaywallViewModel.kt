package com.example.taxflow.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.example.shared2.data.repository.PremiumRepository
import com.example.shared2.billingmanager.BillingManager
import com.example.taxflow.viewModel.uiState.PaywallUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SubscriptionPlan(
    val basePlanId: String,
    val offerToken: String,
    val formattedPrice: String,
    val billingPeriodLabel: String // z. B. "pro Monat" / "pro Jahr"
)



class PaywallViewModel(
    private val billingManager: BillingManager,
    private val premiumRepository: PremiumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState

    private var productDetails: ProductDetails? = null

    init {
        loadOfferings()
        viewModelScope.launch {
            billingManager.purchaseUpdates.collect { purchases ->
                if (billingManager.containsActivePremium(purchases)) {
                    _uiState.value = _uiState.value.copy(purchaseSuccessful = true)
                }
            }
        }
    }

    private fun loadOfferings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val connected = billingManager.connect()
            if (!connected) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Play Store aktuell nicht erreichbar. Bitte später erneut versuchen."
                )
                return@launch
            }

            val details = billingManager.queryPremiumProductDetails()
            productDetails = details as ProductDetails?

            if (details == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Abo-Angebote konnten nicht geladen werden."
                )
                return@launch
            }

            val offers = details.subscriptionOfferDetails.orEmpty()

            val monthly = offers.firstOrNull { it.basePlanId == BillingManager.BASE_PLAN_MONTHLY }
                ?.toSubscriptionPlan("pro Monat")
            val yearly = offers.firstOrNull { it.basePlanId == BillingManager.BASE_PLAN_YEARLY }
                ?.toSubscriptionPlan("pro Jahr")

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                monthlyPlan = monthly,
                yearlyPlan = yearly
            )
        }
    }

    fun purchase(activity: Activity, plan: SubscriptionPlan) {
        val details = productDetails ?: return
        billingManager.launchPurchaseFlow(activity, details, plan.offerToken)
    }

    fun restorePurchases() {
        viewModelScope.launch {
            premiumRepository.refreshFromPlayStore()
        }
    }

    private fun ProductDetails.SubscriptionOfferDetails.toSubscriptionPlan(periodLabel: String): SubscriptionPlan {
        val price = pricingPhases.pricingPhaseList.firstOrNull()?.formattedPrice ?: "–"
        return SubscriptionPlan(
            basePlanId = basePlanId,
            offerToken = offerToken,
            formattedPrice = price,
            billingPeriodLabel = periodLabel
        )
    }
}