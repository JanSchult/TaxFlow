package com.example.taxflow.viewModel.uiState


import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.domain.usecase.VehicleType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.INCOME,
    val amountInput: String = "",
    val note: String = "",
    val selectedCategoryId: Long? = null,
    val selectedCategoryDeductible: Int = 100,  // ← aus Kategorie übernommen
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val useMileageCalculator: Boolean = false,
    val kilometersInput: String = "",
    val vehicleType: VehicleType = VehicleType.CAR
)
