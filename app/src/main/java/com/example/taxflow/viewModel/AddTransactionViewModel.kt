package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.CategoryRepository
import com.example.shared2.data.TransactionRepository
import com.example.shared2.domain.model.Category
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.domain.usecase.VehicleType
import com.example.taxflow.viewModel.uiState.AddTransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    private val receiptDraftHolder: ReceiptDraftHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    val categories: StateFlow<List<Category>> = categoryRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Falls ein Beleg-Scan gerade ein Ergebnis abgelegt hat, direkt übernehmen.
        receiptDraftHolder.consume()?.let { draft ->
            _uiState.value = _uiState.value.copy(
                amountInput = draft.amount?.toString().orEmpty(),
                note = draft.vendorGuess.orEmpty(),
                date = (draft.date ?: _uiState.value.date) as LocalDate
                // Kategorie bewusst nicht automatisch setzen - siehe OCR-Feature-Doku.
            )
        }
    }

    fun onTypeChanged(type: TransactionType) {
        _uiState.value = _uiState.value.copy(
            type = type,
            selectedCategoryId = null,
            useMileageCalculator = false
        )
    }

    fun onAmountChanged(value: String) {
        _uiState.value = _uiState.value.copy(amountInput = value, errorMessage = null)
    }

    fun onNoteChanged(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun onCategorySelected(categoryId: Long) {
        val stillSupportsCalculator = categories.value
            .find { it.id == categoryId }
            ?.supportsMileageCalculator == true

        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            // Rechner ausblenden, wenn eine Kategorie ohne Kilometerpauschale gewählt wird.
            useMileageCalculator = _uiState.value.useMileageCalculator && stillSupportsCalculator
        )
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    // --- Fahrtkostenrechner -------------------------------------------------

    fun onToggleMileageCalculator(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(useMileageCalculator = enabled)
        if (enabled) recalculateMileageAmount()
    }

    fun onKilometersChanged(value: String) {
        _uiState.value = _uiState.value.copy(kilometersInput = value)
        recalculateMileageAmount()
    }

    fun onVehicleTypeChanged(type: VehicleType) {
        _uiState.value = _uiState.value.copy(vehicleType = type)
        recalculateMileageAmount()
    }

    private fun recalculateMileageAmount() {
        val state = _uiState.value
        val km = state.kilometersInput.replace(",", ".").toDoubleOrNull() ?: return
        val amount = km * state.vehicleType.ratePerKm
        _uiState.value = _uiState.value.copy(
            amountInput = "%.2f".format(amount).replace(".", ",")
        )
    }

    // -------------------------------------------------------------------------

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
