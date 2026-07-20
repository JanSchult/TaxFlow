package com.example.taxflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.domain.model.VehicleType
import com.example.taxflow.ui.components.CategoryPickerSheet
import com.example.taxflow.viewModel.AddTransactionViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.core.graphics.toColorInt
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Neue Buchung", style = MaterialTheme.typography.headlineMedium)

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

        OutlinedTextField(
            value = state.amountInput,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Betrag") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            // Wenn der Fahrtkostenrechner aktiv ist, kommt der Betrag aus der km-Berechnung -
            // manuelles Antippen bleibt trotzdem möglich, um das Ergebnis zu übersteuern.
            modifier = Modifier.fillMaxWidth()
        )

        Text("Kategorie", style = MaterialTheme.typography.titleMedium)
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
                    androidx.compose.foundation.layout.Box(
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
            onCategorySelected = viewModel::onCategorySelected,
            onDismiss = { showCategoryPicker = false }
        )
    }
}
private fun parseHexColor(hex: String): Color = try {
    Color(hex.toColorInt())
} catch (e: Exception) {
    Color.Gray
}
