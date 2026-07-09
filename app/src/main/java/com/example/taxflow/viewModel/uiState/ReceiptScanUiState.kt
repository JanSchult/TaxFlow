package com.example.taxflow.viewModel.uiState

import com.example.taxflow.domain.model.ReceiptScanData

data class ReceiptScanUiState(
    val isProcessing: Boolean = false,
    val scanResult: ReceiptScanData? = null,
    val errorMessage: String? = null
)