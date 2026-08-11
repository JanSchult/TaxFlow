package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.VatMode
import com.example.taxflow.ui.components.VatModeCard
import com.example.taxflow.viewModel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateToBackup: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    var taxRate by remember(settings.taxRatePercent) { mutableFloatStateOf(settings.taxRatePercent.toFloat()) }
    var buffer by remember(settings.bufferPercent) { mutableFloatStateOf(settings.bufferPercent.toFloat()) }
    var currency by remember(settings.currencyCode) { mutableStateOf(settings.currencyCode) }
    var savingsGoal by remember(settings.monthlySavingsGoal) { mutableStateOf(settings.monthlySavingsGoal.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("Einstellungen", style = MaterialTheme.typography.headlineMedium)
        }

        // --- EINKOMMENSTEUER ---
        item {
            Text("Einkommensteuer-Rücklage", style = MaterialTheme.typography.titleMedium)
        }

        item {
            Column {
                Text("Geschätzte Steuerquote: ${taxRate.toInt()} %")
                Slider(
                    value = taxRate,
                    onValueChange = { taxRate = it },
                    onValueChangeFinished = { viewModel.updateTaxRate(taxRate.toDouble()) },
                    valueRange = 0f..50f
                )
            }
        }

        item {
            Column {
                Text("Sicherheitspuffer: ${buffer.toInt()} %")
                Slider(
                    value = buffer,
                    onValueChange = { buffer = it },
                    onValueChangeFinished = { viewModel.updateBuffer(buffer.toDouble()) },
                    valueRange = 0f..20f
                )
            }
        }

        item {
            Text(
                "Effektive Rücklagenquote: ${(taxRate + buffer).toInt()} % deines Gewinns",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item { HorizontalDivider() }

        // --- UMSATZSTEUER ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Umsatzsteuer", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Bist du Regelbesteuerer oder Kleinunternehmer nach §19 UStG?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(VatMode.entries.size) { index ->
            val mode = VatMode.entries[index]
            VatModeCard(
                mode = mode,
                isSelected = settings.vatMode == mode,
                onClick = { viewModel.updateVatMode(mode) }
            )
        }

        item { HorizontalDivider() }

        // --- WEITERE EINSTELLUNGEN ---
        item {
            Text("Weitere Einstellungen", style = MaterialTheme.typography.titleMedium)
        }

        item {
            OutlinedTextField(
                value = currency,
                onValueChange = {
                    currency = it
                    if (it.length == 3) viewModel.updateCurrency(it.uppercase())
                },
                label = { Text("Währung (z. B. EUR, CHF, USD)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = savingsGoal,
                onValueChange = {
                    savingsGoal = it
                    it.replace(",", ".").toDoubleOrNull()
                        ?.let { v -> viewModel.updateSavingsGoal(v) }
                },
                label = { Text("Monatliches Sparziel (€)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { HorizontalDivider() }

        item {
            Button(
                onClick = onNavigateToBackup,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Backup & Wiederherstellung")
            }
        }
    }
}