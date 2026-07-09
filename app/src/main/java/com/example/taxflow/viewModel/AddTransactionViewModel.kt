package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.data.repository.CategoryRepository
import com.example.taxflow.data.repository.TransactionRepository
import com.example.taxflow.domain.model.Category
import com.example.taxflow.domain.model.Transaction
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.viewModel.uiState.AddTransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    private val receiptDraftHolder: ReceiptDraftHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    init {
        receiptDraftHolder.consume()?.let { draft ->
            _uiState.value = _uiState.value.copy(
                amountInput = draft.amount?.toString().orEmpty(),
                note = draft.vendorGuess.orEmpty(),
                date = draft.date ?: _uiState.value.date
                // Kategorie bewusst NICHT automatisch setzen – das soll der Nutzer
                // weiterhin selbst wählen, da eine verlässliche Kategorie-Zuordnung
                // aus dem Belegtext allein nicht robust genug ist.
            )
        }
    }

    val categories: StateFlow<List<Category>> = categoryRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onTypeChanged(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type, selectedCategoryId = null)
    }

    fun onAmountChanged(value: String) {
        _uiState.value = _uiState.value.copy(amountInput = value, errorMessage = null)
    }

    fun onNoteChanged(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountInput.replace(",", ".").toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            _uiState.value = state.copy(errorMessage = "Bitte einen gültigen Betrag eingeben.")
            return
        }
        if (state.selectedCategoryId == null) {
            _uiState.value = state.copy(errorMessage = "Bitte eine Kategorie auswählen.")
            return
        }

        viewModelScope.launch {
            transactionRepository.add(
                Transaction(
                    amount = amount,
                    type = state.type,
                    categoryId = state.selectedCategoryId,
                    date = state.date,
                    note = state.note
                )
            )
            _uiState.value = AddTransactionUiState(isSaved = true)
        }
    }
}