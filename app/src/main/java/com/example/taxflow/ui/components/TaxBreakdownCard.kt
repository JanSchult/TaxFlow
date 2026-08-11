package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TaxReserveResult
import com.example.taxflow.util.formatCurrency

/**
 * Ersetzt die einfache "Steuerrücklage"-Karte durch eine aufgeklappte
 * Aufschlüsselung: Gewinn → Einkommensteuer-Rücklage → Sparziel → Frei verfügbar.
 * So sieht der Nutzer genau, wie sich das Ergebnis zusammensetzt, statt nur
 * die finale Zahl zu sehen.
 */
@Composable
fun TaxBreakdownCard(
    result: TaxReserveResult,
    currencyCode: String,
    taxRatePercent: Double,
    bufferPercent: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Steuer- & Rücklagenübersicht",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            BreakdownRow(
                label = "Einnahmen",
                value = formatCurrency(result.totalIncome, currencyCode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            BreakdownRow(
                label = "− Ausgaben",
                value = "− ${formatCurrency(result.totalExpenses, currencyCode)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            BreakdownRow(
                label = "= Gewinn (Basis für ESt)",
                value = formatCurrency(result.profit, currencyCode),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            BreakdownRow(
                label = "Einkommensteuer-Rücklage\n(${taxRatePercent.toInt()} % + ${bufferPercent.toInt()} % Puffer)",
                value = formatCurrency(result.taxReserveAmount, currencyCode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            if (result.savingsGoalAmount > 0) {
                BreakdownRow(
                    label = "Sparziel",
                    value = formatCurrency(result.savingsGoalAmount, currencyCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            BreakdownRow(
                label = "✓ Frei verfügbar",
                value = formatCurrency(result.availableAfterReserve, currencyCode),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    style: androidx.compose.ui.text.TextStyle,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = style, color = color, modifier = Modifier.weight(1f))
        Text(value, style = style, color = color)
    }
}