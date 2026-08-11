package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import com.example.shared2.data.repository.PremiumRepository

class PremiumStatusViewModel(repository: PremiumRepository) : ViewModel() {
    val isPremium = repository.isPremium
}