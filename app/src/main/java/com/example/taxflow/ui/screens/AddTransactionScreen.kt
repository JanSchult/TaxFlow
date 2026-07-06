package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.taxflow.domain.model.TransactionType
import com.example.taxflow.viewModel.AddTransactionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTransactionScreen(
    onSaved: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    val filteredCategories = categories.filter { it.type == state.type }

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
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Kategorie", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredCategories) { category ->
                FilterChip(
                    selected = state.selectedCategoryId == category.id,
                    onClick = { viewModel.onCategorySelected(category.id) },
                    label = { Text(category.name) }
                )
            }
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
    }
}