package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TaxReserveResult
import com.example.taxflow.util.formatCurrency

@Composable
fun TaxReserveCard(result: TaxReserveResult, currencyCode: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Steuerrücklage diesen Monat",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatCurrency(result.taxReserveAmount, currencyCode),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Basierend auf ${formatCurrency(result.profit, currencyCode)} Gewinn diesen Monat",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
