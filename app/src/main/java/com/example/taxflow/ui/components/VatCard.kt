package com.example.taxflow.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.VatResult
import com.example.taxflow.util.formatCurrency

/**
 * Umsatzsteuer-Rücklagen-Karte für das Dashboard.
 * Nur sichtbar, wenn Regelbesteuerung aktiv (vatResult != null).
 * Zeigt Brutto → USt-Anteil (ans FA) → Netto klar aufgegliedert.
 */
@Composable
fun VatCard(
    vatResult: VatResult?,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = vatResult != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val result = vatResult ?: return@AnimatedVisibility

        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Umsatzsteuer-Rücklage (${result.vatRatePercent.toInt()} %)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                VatRow(
                    label = "Bruttoeinnahmen",
                    value = formatCurrency(result.grossIncome, currencyCode),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.2f)
                )

                VatRow(
                    label = "davon USt ${result.vatRatePercent.toInt()} % → Finanzamt",
                    value = formatCurrency(result.vatAmount, currencyCode),
                    isHighlighted = true,
                    color = MaterialTheme.colorScheme.error
                )

                VatRow(
                    label = "Nettoeinnahmen (dein Anteil)",
                    value = formatCurrency(result.netIncome, currencyCode),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Text(
                    "Hinweis: Diese Rücklage ist eine Orientierungshilfe für deine Liquidität. " +
                            "Die genaue USt-Zahllast (inkl. Vorsteuerverrechnung) ergibt sich erst aus " +
                            "der Umsatzsteuer-Voranmeldung.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun VatRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (isHighlighted) MaterialTheme.typography.titleSmall
            else MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = if (isHighlighted) MaterialTheme.typography.titleMedium
            else MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}