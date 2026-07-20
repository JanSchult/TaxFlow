package com.example.taxflow.viewModel.uiState

import com.example.taxflow.domain.model.Category
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.domain.model.VehicleType
import java.time.LocalDate

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.INCOME,
    val amountInput: String = "",
    val note: String = "",
    val selectedCategoryId: Long? = null,
    val date: LocalDate = LocalDate.now(),
    val categories: List<Category> = emptyList(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    // Fahrtkostenrechner
    val useMileageCalculator: Boolean = false,
    val kilometersInput: String = "",
    val vehicleType: VehicleType = VehicleType.CAR
)