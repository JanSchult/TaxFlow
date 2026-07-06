package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.repository.SettingsRepository
import com.example.taxflow.data.repository.TransactionRepository
import com.example.taxflow.domain.model.UserSettings
import com.example.taxflow.domain.usecase.CalculateTaxReserveUseCase
import com.example.taxflow.viewModel.uiState.DashboardUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository,
    private val calculateTaxReserveUseCase: CalculateTaxReserveUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionRepository.getBetween(startOfMonth(), endOfMonth()),
        settingsRepository.settings
    ) { transactions, settings: UserSettings ->
        DashboardUiState(
            currentMonthLabel = monthLabel(),
            result = calculateTaxReserveUseCase(transactions, settings),
            currencyCode = settings.currencyCode,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun startOfMonth(): LocalDate = LocalDate.now().withDayOfMonth(1)
    private fun endOfMonth(): LocalDate {
        val now = LocalDate.now()
        return now.withDayOfMonth(now.lengthOfMonth())
    }

    private fun monthLabel(): String {
        val now = LocalDate.now()
        val months = listOf(
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
        )
        return "${months[now.monthValue - 1]} ${now.year}"
    }
}