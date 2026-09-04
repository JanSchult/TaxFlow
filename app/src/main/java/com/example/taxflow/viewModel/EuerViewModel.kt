package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.repository.SettingsRepository
import com.example.shared2.data.repository.TransactionRepository
import com.example.shared2.domain.model.EuerPeriodMode
import com.example.shared2.usecase.BuildEuerReportUseCase
import com.example.taxflow.viewModel.uiState.EuerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

 class EuerViewModel(
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository,
    private val buildEuerReportUseCase: BuildEuerReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EuerUiState())
    val uiState: StateFlow<EuerUiState> = _uiState

    init { loadReport() }

    fun setPeriodMode(mode: EuerPeriodMode) {
        _uiState.update { it.copy(periodMode = mode) }
        loadReport()
    }

    fun setMonth(month: Int) {
        _uiState.update { it.copy(selectedMonth = month) }
        loadReport()
    }

    fun setYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        loadReport()
    }

    private fun loadReport() {
        val state = _uiState.value
        val (from, to, label) = periodRange(state)

        combine(
            transactionRepository.getBetween(from, to),
            settingsRepository.settings
        ) { transactions, settings ->
            buildEuerReportUseCase(transactions, settings, label)
        }.onEach { report ->
            _uiState.update { it.copy(report = report, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    private fun periodRange(state: EuerUiState): Triple<LocalDate, LocalDate, String> {
        val monthNames = listOf("Januar","Februar","März","April","Mai","Juni",
            "Juli","August","September","Oktober","November","Dezember")
        return when (state.periodMode) {
            EuerPeriodMode.MONTH -> {
                val first = LocalDate(state.selectedYear, state.selectedMonth, 1)
                val last = first.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
                Triple(first, last, "${monthNames[state.selectedMonth - 1]} ${state.selectedYear}")
            }
            EuerPeriodMode.YEAR -> {
                val first = LocalDate(state.selectedYear, 1, 1)
                val last = LocalDate(state.selectedYear, 12, 31)
                Triple(first, last, "Jahr ${state.selectedYear}")
            }
        }
    }
}