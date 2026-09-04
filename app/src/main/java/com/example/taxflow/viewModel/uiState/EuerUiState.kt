package com.example.taxflow.viewModel.uiState

import com.example.shared2.domain.model.EuerPeriodMode
import com.example.shared2.domain.model.EuerReport
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class EuerUiState(
    val periodMode: EuerPeriodMode = EuerPeriodMode.MONTH,
    val selectedYear: Int = Clock.System.todayIn(TimeZone.currentSystemDefault()).year,
    val selectedMonth: Int = Clock.System.todayIn(TimeZone.currentSystemDefault()).monthNumber,
    val report: EuerReport? = null,
    val isLoading: Boolean = true
)
