package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.PdfReportGenerator
import com.example.taxflow.data.repository.CategoryRepository
import com.example.taxflow.data.repository.TransactionRepository
import com.example.taxflow.domain.model.ExportMode
import com.example.taxflow.domain.model.ExportPeriodType
import com.example.taxflow.viewModel.uiState.ExportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

class ExportViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val pdfGenerator: PdfReportGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState

    fun setPeriodType(type: ExportPeriodType) {
        _uiState.value = _uiState.value.copy(periodType = type)
    }

    fun setMode(mode: ExportMode) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun generate() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, errorMessage = null, generatedFiles = null)
            try {
                val today = LocalDate.now()
                val (from, to, label) = when (_uiState.value.periodType) {
                    ExportPeriodType.MONTH -> {
                        val ym = YearMonth.from(today)
                        Triple(ym.atDay(1), ym.atEndOfMonth(), "Monat ${ym.monthValue}/${ym.year}")
                    }
                    ExportPeriodType.YEAR -> {
                        Triple(LocalDate.of(today.year, 1, 1), LocalDate.of(today.year, 12, 31), "Jahr ${today.year}")
                    }
                }

                val transactions = transactionRepository.getBetween(from, to).first()
                val categories = categoryRepository.getAll().first()

                if (transactions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        errorMessage = "Für diesen Zeitraum liegen keine Buchungen vor."
                    )
                    return@launch
                }

                val files: List<File> = when (_uiState.value.mode) {
                    ExportMode.SUMMARY -> listOf(pdfGenerator.generateSummaryReport(transactions, categories, label))
                    ExportMode.INDIVIDUAL -> pdfGenerator.generateIndividualReceipts(transactions, categories)
                }

                _uiState.value = _uiState.value.copy(isGenerating = false, generatedFiles = files)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = "Export fehlgeschlagen. Bitte erneut versuchen."
                )
            }
        }
    }

    /** Nach dem Teilen aufrufen, damit derselbe Export nicht erneut ausgelöst wird. */
    fun clearResult() {
        _uiState.value = _uiState.value.copy(generatedFiles = null)
    }
}
