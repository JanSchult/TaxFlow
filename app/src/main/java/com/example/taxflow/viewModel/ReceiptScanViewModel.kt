package com.example.taxflow.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.orc.ReceiptTextRecognizer
import com.example.taxflow.viewModel.uiState.ReceiptScanUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.data.orc.ReceiptParser
import com.example.taxflow.domain.usecase.ReceiptScanData


class ReceiptScanViewModel(
    private val recognizer: ReceiptTextRecognizer,
    private val draftHolder: ReceiptDraftHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptScanUiState())
    val uiState: StateFlow<ReceiptScanUiState> = _uiState

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, errorMessage = null)
            try {
                val rawText = recognizer.recognize(bitmap)
                val parsed = ReceiptParser.parse(rawText)
                _uiState.value = _uiState.value.copy(isProcessing = false, scanResult = parsed)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    errorMessage = "Beleg konnte nicht gelesen werden. Bitte erneut versuchen (auf gute Beleuchtung und Fokus achten)."
                )
            }
        }
    }

    /** Manuelle Korrektur eines erkannten Werts, bevor er übernommen wird. */
    fun updateResult(update: (ReceiptScanData) -> ReceiptScanData) {
        _uiState.value.scanResult?.let { current ->
            _uiState.value = _uiState.value.copy(scanResult = update(current))
        }
    }

    fun confirmAndPublish() {
        _uiState.value.scanResult?.let { draftHolder.publish(it) }
    }

    fun reset() {
        _uiState.value = ReceiptScanUiState()
    }
}