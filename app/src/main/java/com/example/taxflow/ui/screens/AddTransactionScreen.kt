package com.example.taxflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.taxflow.ui.components.CategoryPickerSheet
import com.example.taxflow.viewModel.AddTransactionViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.core.graphics.toColorInt
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.VatMode
import com.example.taxflow.ui.components.MileageCalculatorCard

@Composable
fun AddTransactionScreen(
    onSaved: () -> Unit,
    onScanReceiptClick: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showCategoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    val filteredCategories = categories.filter { it.type == state.type }
    val selectedCategory = filteredCategories.find { it.id == state.selectedCategoryId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Neue Buchung", style = MaterialTheme.typography.headlineMedium)

        // Einnahme / Ausgabe Wahl
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = state.type == TransactionType.INCOME,
                onClick = { viewModel.onTypeChanged(TransactionType.INCOME) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Einnahme") }
            SegmentedButton(
                selected = state.type == TransactionType.EXPENSE,
                onClick = { viewModel.onTypeChanged(TransactionType.EXPENSE) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Ausgabe") }
        }

        // Brutto-Betrag Eingabe
        OutlinedTextField(
            value = state.amountInput,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Betrag (Brutto)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        // --- NEU: USt-Satz Auswahl ---
        Text("Umsatzsteuer", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val modes = listOf(VatMode.STANDARD, VatMode.REDUCED, VatMode.NONE)
            modes.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = state.vatMode == mode,
                    onClick = { viewModel.onVatModeChanged(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
                ) {
                    Text(
                        when (mode) {
                            VatMode.STANDARD -> "19 %"
                            VatMode.REDUCED -> "7 %"
                            VatMode.NONE -> "0 % / Keine"
                        }
                    )
                }
            }
        }

        // Live USt & Netto Vorschau
        if (state.grossAmount > 0.0 && state.vatMode != VatMode.NONE) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Netto: ${"%.2f".format(state.netAmount)} €",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "USt (${state.vatMode.ratePercent?.toInt() ?: 0}%): ${"%.2f".format(state.vatAmount)} €",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Kategorie-Auswahl
        Text("Kategorie", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCategoryPicker = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedCategory != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(parseHexColor(selectedCategory.colorHex), CircleShape)
                        )
                        Text(
                            selectedCategory.name,
                            modifier = Modifier.padding(start = 10.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text(
                        "Kategorie auswählen",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Filled.ExpandMore, contentDescription = "Kategorie wählen")
            }

            // Anzeige der steuerlichen Absetzbarkeit (§ 4 Abs. 5 EStG)
            if (selectedCategory != null && state.type == TransactionType.EXPENSE) {
                Text(
                    text = "Steuerlich zu ${state.selectedCategoryDeductible}% absetzbar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Fahrtenbuch / Km-Rechner (falls Kategorie dies unterstützt)
        if (selectedCategory?.supportsMileageCalculator == true) {
            MileageCalculatorCard(
                useCalculator = state.useMileageCalculator,
                kilometers = state.kilometersInput,
                vehicleType = state.vehicleType,
                onToggle = viewModel::onToggleMileageCalculator,
                onKilometersChanged = viewModel::onKilometersChanged,
                onVehicleTypeChanged = viewModel::onVehicleTypeChanged
            )
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::onNoteChanged,
            label = { Text("Notiz (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
            Text("Speichern")
        }

        OutlinedButton(onClick = onScanReceiptClick, modifier = Modifier.fillMaxWidth()) {
            Text("📷 Beleg scannen statt manuell eintippen")
        }
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            categories = filteredCategories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = { categoryId ->
                val category = filteredCategories.find { it.id == categoryId }
                category?.let { viewModel.onCategorySelected(it) }
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }
}

private fun parseHexColor(hex: String): Color = try {
    Color(hex.toColorInt())
} catch (e: Exception) {
    Color.Gray
}