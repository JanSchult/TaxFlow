package com.example.taxflow.viewModel.uiState


import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.VatMode
import com.example.taxflow.domain.usecase.VehicleType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.INCOME,
    val amountInput: String = "",

    // --- NEU: Dynamisch berechnete Werte für die UI-Vorschau ---
    val grossAmount: Double = 0.0,
    val netAmount: Double = 0.0,
    val vatAmount: Double = 0.0,
    val vatMode: VatMode = VatMode.STANDARD, // Bzw. aus Settings geladen

    val note: String = "",
    val selectedCategoryId: Long? = null,
    val selectedCategoryDeductible: Int = 100, // Steuerliche Absetzbarkeit (§ 4 Abs. 5 EStG)
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),

    val isSaved: Boolean = false,
    val errorMessage: String? = null,

    // KM-Rechner / Fahrtkosten
    val useMileageCalculator: Boolean = false,
    val kilometersInput: String = "",
    val vehicleType: VehicleType = VehicleType.CAR
)
