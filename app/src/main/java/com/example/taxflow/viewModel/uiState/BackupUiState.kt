package com.example.taxflow.viewModel.uiState

data class BackupUiState(
    val isWorking: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
    /** Sobald gesetzt, öffnet die UI den "Speichern unter"-Dialog (SAF). */
    val exportedJson: String? = null
)