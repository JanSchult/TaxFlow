package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.EuerReport

@Composable
fun EuerResultCard(report: EuerReport) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Ergebnis ${report.periodLabel}", style = MaterialTheme.typography.titleMedium)
            EuerRow("Steuerlicher Gewinn (Basis EStG-Rücklage)", report.taxableProfit,
                bold = true, color = MaterialTheme.colorScheme.primary)
            EuerRow("Cashflow-Gewinn (tatsächlich verfügbar)", report.realProfit,
                bold = true, color = MaterialTheme.colorScheme.secondary)
            Text(
                "Steuerlicher Gewinn = abziehbare Ausgaben berücksichtigt.\n" +
                        "Cashflow-Gewinn = alle tatsächlichen Ausgaben abgezogen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}