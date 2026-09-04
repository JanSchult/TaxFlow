package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.repository.CategoryRepository
import com.example.shared2.data.repository.SettingsRepository
import com.example.shared2.data.repository.TransactionRepository
import com.example.shared2.domain.model.Category
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.VatMode
import com.example.shared2.usecase.BuildEuerReportUseCase
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.domain.usecase.VehicleType
import com.example.taxflow.viewModel.uiState.AddTransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository,
    private val receiptDraftHolder: ReceiptDraftHolder
 ) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    val categories: StateFlow<List<Category>> = categoryRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        receiptDraftHolder.consume()?.let { draft ->
            _uiState.value = _uiState.value.copy(
                amountInput = draft.amount?.toString().orEmpty(),
                note = draft.vendorGuess.orEmpty(),
                date = (draft.date ?: _uiState.value.date) as LocalDate
            )
        }
    }

    fun onTypeChanged(type: TransactionType) {
        _uiState.value = _uiState.value.copy(
            type = type,
            selectedCategoryId = null,
            selectedCategoryDeductible = 100,
            useMileageCalculator = false
        )
    }

    fun onAmountChanged(value: String) {
        _uiState.value = _uiState.value.copy(amountInput = value, errorMessage = null)
    }

    fun onNoteChanged(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun onCategorySelected(category: Category) {
        val stillSupportsCalculator = category.supportsMileageCalculator
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = category.id,
            selectedCategoryDeductible = category.taxDeductiblePercentage,
            useMileageCalculator = _uiState.value.useMileageCalculator && stillSupportsCalculator
        )
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

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
        val km = _uiState.value.kilometersInput.replace(",", ".").toDoubleOrNull() ?: return
        val amount = km * _uiState.value.vehicleType.ratePerKm
        _uiState.value = _uiState.value.copy(
            amountInput = "%.2f".format(amount).replace(".", ",")
        )
    }

    fun onVatModeChanged(newMode: VatMode) {
        _uiState.update { currentState ->
            val gross = currentState.amountInput.replace(",", ".").toDoubleOrNull() ?: 0.0
            val net = calculateNet(gross, newMode)
            val vat = gross - net

            currentState.copy(
                vatMode = newMode,
                grossAmount = gross,
                netAmount = net,
                vatAmount = vat
            )
        }
    }

    private fun calculateNet(gross: Double, vatMode: VatMode): Double {
        val rate = vatMode.ratePercent ?: return gross
        return gross / (1.0 + (rate / 100.0))
    }

    fun save() {
        val state = _uiState.value
        val grossAmount = state.amountInput.replace(",", ".").toDoubleOrNull()

        if (grossAmount == null || grossAmount <= 0.0) {
            _uiState.value = state.copy(errorMessage = "Bitte einen gültigen Betrag eingeben.")
            return
        }
        val categoryId = state.selectedCategoryId
        if (categoryId == null) {
            _uiState.value = state.copy(errorMessage = "Bitte eine Kategorie auswählen.")
            return
        }

        viewModelScope.launch {
            val currentSettings = settingsRepository.settings.first()
            val vatMode = currentSettings.vatMode
            val vatRate = vatMode.ratePercent?.div(100.0) ?: 0.0
            val netAmount = if (vatRate > 0.0) grossAmount / (1.0 + vatRate) else grossAmount
            val vatAmount = grossAmount - netAmount

            // categoryName für EÜR-Gruppierung mitgeben
            val categoryName = categories.value
                .find { it.id == categoryId }?.name ?: ""

            transactionRepository.add(
                Transaction(
                    grossAmount = grossAmount,
                    netAmount = netAmount,
                    vatAmount = vatAmount,
                    vatMode = vatMode,
                    type = state.type,
                    categoryId = categoryId,
                    categoryName = categoryName,   // ← neu
                    date = state.date,
                    note = state.note,
                    taxDeductiblePercentage = state.selectedCategoryDeductible
                )
            )
            _uiState.value = AddTransactionUiState(isSaved = true)
        }
    }
}