package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.repository.TransactionRepository
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.domain.usecase.MonthSummary
import com.example.taxflow.domain.usecase.OverviewMode
import com.example.taxflow.viewModel.uiState.OverviewUiState
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*

class OverviewViewModel(
    transactionRepository: TransactionRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(OverviewMode.MONTH)
    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _customFrom = MutableStateFlow<LocalDate?>(null)
    private val _customTo = MutableStateFlow<LocalDate?>(null)

    val uiState: StateFlow<OverviewUiState> = combine(
        transactionRepository.getAll(),
        _mode,
        _selectedCategories,
        _selectedType,
        _customFrom
    ) { transactions, mode, selectedCats, selectedType, customFrom ->
        // alle Kategorienamen für den Filter-Dialog sammeln
        val availableCategories = transactions
            .asSequence()
            .filter { _selectedType.value == null || it.type == _selectedType.value }
            .map { it.categoryName }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .toList()

        // Filter anwenden
        val filtered = transactions.filter { tx ->
            val catMatch = selectedCats.isEmpty() || tx.categoryName in selectedCats
            val typeMatch = selectedType == null || tx.type == selectedType
            val fromMatch = customFrom == null || tx.date >= customFrom
            val toMatch = _customTo.value == null || tx.date <= _customTo.value!!
            catMatch && typeMatch && fromMatch && toMatch
        }

        OverviewUiState(
            mode = mode,
            monthSummaries = buildSummaries(filtered, mode),
            isLoading = false,
            availableCategories = availableCategories,
            selectedCategories = selectedCats,
            selectedType = selectedType,
            customFrom = customFrom,
            customTo = _customTo.value,
            isFilterActive = selectedCats.isNotEmpty()
                    || selectedType != null
                    || customFrom != null
                    || _customTo.value != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewUiState())

    fun setMode(mode: OverviewMode) { _mode.value = mode }

    fun toggleCategory(name: String) {
        _selectedCategories.update {
            if (name in it) it - name else it + name
        }
    }

    fun setType(type: TransactionType?) { _selectedType.value = type }

    fun setDateRange(from: LocalDate?, to: LocalDate?) {
        _customFrom.value = from
        _customTo.value = to
    }

    fun clearFilters() {
        _selectedCategories.value = emptySet()
        _selectedType.value = null
        _customFrom.value = null
        _customTo.value = null
    }

    // buildSummaries bleibt exakt wie gehabt
    private fun buildSummaries(transactions: List<Transaction>, mode: OverviewMode): List<MonthSummary> {
        val monthNames = listOf("Jan","Feb","Mär","Apr","Mai","Jun","Jul","Aug","Sep","Okt","Nov","Dez")
        return when (mode) {
            OverviewMode.MONTH -> {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val currentFirstOfMonth = LocalDate(today.year, today.monthNumber, 1)
                (5 downTo 0).map { offset ->
                    val targetDate = currentFirstOfMonth.minus(DatePeriod(months = offset))
                    val monthTx = transactions.filter {
                        it.date.year == targetDate.year && it.date.monthNumber == targetDate.monthNumber
                    }
                    val income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expenses = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    MonthSummary(
                        label = "${monthNames[targetDate.monthNumber - 1]} ${targetDate.year % 100}",
                        income = income, expenses = expenses, profit = income - expenses
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