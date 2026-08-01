package com.example.taxflow.viewModel.uiState


import com.example.shared2.domain.model.Category
import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.domain.usecase.VehicleType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
data class AddTransactionUiState(
    val type: TransactionType = TransactionType.INCOME,
    val amountInput: String = "",
    val note: String = "",
    val selectedCategoryId: Long? = null,
    val date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val categories: List<Category> = emptyList(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    // Fahrtkostenrechner
    val useMileageCalculator: Boolean = false,
    val kilometersInput: String = "",
    val vehicleType: VehicleType = VehicleType.CAR
)