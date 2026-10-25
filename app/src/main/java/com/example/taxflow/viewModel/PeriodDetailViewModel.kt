package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.repository.TransactionRepository
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.viewModel.uiState.PeriodDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class PeriodDetailViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _label = MutableStateFlow("")
    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _from = MutableStateFlow<LocalDate?>(null)
    private val _to = MutableStateFlow<LocalDate?>(null)
    private val _showFilter = MutableStateFlow(false)

    val uiState: StateFlow<PeriodDetailUiState> = combine(
        _from,
        _to,
        _selectedCategories,
        _selectedType,
        _showFilter
    ) { from, to, selectedCats, selectedType, showFilter ->
        if (from == null || to == null) return@combine PeriodDetailUiState()

        val all = transactionRepository.getBetween(from, to).first()

        val incomeCategories = all
            .filter { it.type == TransactionType.INCOME }
            .map { it.categoryName }.filter { it.isNotBlank() }.distinct().sorted()

        val expenseCategories = all
            .filter { it.type == TransactionType.EXPENSE }
            .map { it.categoryName }.filter { it.isNotBlank() }.distinct().sorted()

        val filtered = all.filter { tx ->
            val catMatch = selectedCats.isEmpty() || tx.categoryName in selectedCats
            val typeMatch = selectedType == null || tx.type == selectedType
            catMatch && typeMatch
        }

        PeriodDetailUiState(
            label = _label.value,
            transactions = all,
            filteredTransactions = filtered,
            incomeCategories = incomeCategories,
            expenseCategories = expenseCategories,
            selectedCategories = selectedCats,
            selectedType = selectedType,
            totalIncome = filtered.filter { it.type == TransactionType.INCOME }
                .sumOf { it.grossAmount },
            totalExpenses = filtered.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.grossAmount },
            totalVat = filtered.sumOf { it.vatAmount },
            isLoading = false,
            showFilter = showFilter
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PeriodDetailUiState())

    fun init(label: String, from: LocalDate, to: LocalDate) {
        _label.value = label
        _from.value = from
        _to.value = to
    }

    fun toggleCategory(name: String) {
        _selectedCategories.update { if (name in it) it - name else it + name }
    }

    fun setType(type: TransactionType?) { _selectedType.value = type }
    fun clearFilters() { _selectedCategories.value = emptySet(); _selectedType.value = null }
    fun toggleFilter() { _showFilter.update { !it } }
}