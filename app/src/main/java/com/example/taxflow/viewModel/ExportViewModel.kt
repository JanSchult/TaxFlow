package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared2.data.repository.CategoryRepository
import com.example.shared2.data.repository.TransactionRepository
import com.example.taxflow.data.PdfReportGenerator
import com.example.taxflow.domain.usecase.ExportMode
import com.example.taxflow.domain.usecase.ExportPeriodType
import com.example.taxflow.viewModel.uiState.ExportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.io.File
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

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
                // 1. Heutiges Datum holen
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                val (from, to, label) = when (_uiState.value.periodType) {
                    ExportPeriodType.MONTH -> {
                        // Erster Tag des aktuellen Monats:
                        val firstDay = LocalDate(today.year, today.monthNumber, 1)

                        // Letzter Tag des aktuellen Monats: (1. des nächsten Monats minus 1 Tag)
                        val nextMonthFirstDay = firstDay.plus(DatePeriod(months = 1))
                        val lastDay = nextMonthFirstDay.minus(DatePeriod(days = 1))

                        Triple(
                            firstDay,
                            lastDay,
                            "Monat ${today.monthNumber}/${today.year}"
                        )
                    }
                    ExportPeriodType.YEAR -> {
                        Triple(
                            LocalDate(today.year, 1, 1),
                            LocalDate(today.year, 12, 31),
                            "Jahr ${today.year}"
                        )
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
