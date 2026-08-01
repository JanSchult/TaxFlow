package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.SettingsRepository
import com.example.shared2.data.TransactionRepository
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.usecase.CalculateTaxReserveUseCase
import com.example.taxflow.viewModel.uiState.DashboardUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
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

    private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    private fun startOfMonth(): LocalDate {
        val now = today()
        return LocalDate(now.year, now.monthNumber, 1)
    }

    private fun endOfMonth(): LocalDate {
        val firstOfThisMonth = startOfMonth()
        val firstOfNextMonth = firstOfThisMonth.plus(DatePeriod(months = 1))
        return firstOfNextMonth.minus(DatePeriod(days = 1))
    }

    private fun monthLabel(): String {
        val now = today()
        val months = listOf(
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
        )
        return "${months[now.monthNumber - 1]} ${now.year}"
    }
}