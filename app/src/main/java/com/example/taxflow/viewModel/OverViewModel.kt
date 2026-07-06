package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.repository.TransactionRepository
import com.example.taxflow.domain.model.MonthSummary
import com.example.taxflow.domain.model.OverviewMode
import com.example.taxflow.domain.model.Transaction
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.viewModel.uiState.OverviewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

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
                val now = YearMonth.now()
                (5 downTo 0).map { offset ->
                    val ym = now.minusMonths(offset.toLong())
                    val monthTx = transactions.filter {
                        YearMonth.from(it.date) == ym
                    }
                    MonthSummary(
                        label = "${monthNames[ym.monthValue - 1]} ${ym.year % 100}",
                        income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                        expenses = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                        profit = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                                monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    )
                }
            }
            OverviewMode.YEAR -> {
                val currentYear = LocalDate.now().year
                (2 downTo 0).map { offset ->
                    val year = currentYear - offset
                    val yearTx = transactions.filter { it.date.year == year }
                    MonthSummary(
                        label = year.toString(),
                        income = yearTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                        expenses = yearTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                        profit = yearTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                                yearTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    )
                }
            }
        }
    }
}