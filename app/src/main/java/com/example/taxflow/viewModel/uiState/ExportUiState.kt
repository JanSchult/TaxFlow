package com.example.taxflow.viewModel.uiState


import com.example.taxflow.domain.usecase.ExportMode
import com.example.taxflow.domain.usecase.ExportPeriodType
import java.io.File

data class ExportUiState(
    val periodType: ExportPeriodType = ExportPeriodType.MONTH,
    val mode: ExportMode = ExportMode.SUMMARY,
    val isGenerating: Boolean = false,
    val generatedFiles: List<File>? = null,
    val errorMessage: String? = null
)