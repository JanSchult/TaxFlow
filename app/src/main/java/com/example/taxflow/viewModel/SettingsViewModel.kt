package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.repository.SettingsRepository
import com.example.taxflow.domain.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<UserSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings(taxRatePercent = 19.0))

    fun updateTaxRate(value: Double) = updateAndSave { it.copy(taxRatePercent = value) }
    fun updateCurrency(value: String) = updateAndSave { it.copy(currencyCode = value) }
    fun updateSavingsGoal(value: Double) = updateAndSave { it.copy(monthlySavingsGoal = value) }
    fun updateBuffer(value: Double) = updateAndSave { it.copy(bufferPercent = value) }

    private fun updateAndSave(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch {
            repository.update(transform(settings.value))
        }
    }
}