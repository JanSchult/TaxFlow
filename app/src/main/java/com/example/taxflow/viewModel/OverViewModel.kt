package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.TransactionRepository
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.domain.usecase.MonthSummary
import com.example.taxflow.domain.usecase.OverviewMode
import com.example.taxflow.viewModel.uiState.OverviewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class OverviewViewModel(
transactionRepository: TransactionRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(OverviewMode.MONTH)

    val uiState: StateFlow<OverviewUiState> = combine(
        transactionRepository.getAll(),
        _mode
    ) { transactions, mode ->
        OverviewUiState(
            mode = mode,
            monthSummaries = buildSummaries(transactions, mode),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewUiState())

    fun setMode(mode: OverviewMode) {
        _mode.value = mode
    }

    private fun buildSummaries(transactions: List<Transaction>, mode: OverviewMode): List<MonthSummary> {
        val monthNames = listOf(
            "Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"
        )

        return when (mode) {
            OverviewMode.MONTH -> {
                // Aktuelles Datum in der lokalen Zeitzone holen
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                // Auf den 1. des aktuellen Monats setzen, damit Datumsberechnungen exakt sind
                val currentFirstOfMonth = LocalDate(today.year, today.monthNumber, 1)

                (5 downTo 0).map { offset ->
                    // Vormonate mit DatePeriod(months = ...) abziehen
                    val targetDate = currentFirstOfMonth.minus(DatePeriod(months = offset))

                    val targetYear = targetDate.year
                    val targetMonthNumber = targetDate.monthNumber

                    // Filtern nach Jahr und Monat
                    val monthTx = transactions.filter {
                        it.date.year == targetYear && it.date.monthNumber == targetMonthNumber
                    }

                    val income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expenses = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                    MonthSummary(
                        label = "${monthNames[targetMonthNumber - 1]} ${targetYear % 100}",
                        income = income,
                        expenses = expenses,
                        profit = income - expenses
                    )
                }
            }
            OverviewMode.YEAR -> {
                val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

                (2 downTo 0).map { offset ->
                    val year = currentYear - offset
                    val yearTx = transactions.filter { it.date.year == year }

                    val income = yearTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expenses = yearTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                    MonthSummary(
                        label = year.toString(),
                        income = income,
                        expenses = expenses,
                        profit = income - expenses
                    )
                }
            }
        }
    }
}