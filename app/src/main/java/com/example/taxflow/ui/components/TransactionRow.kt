package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.VatMode
import com.example.taxflow.util.formatCurrency

@Composable
 fun TransactionRow(tx: com.example.shared2.domain.model.Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tx.categoryName.ifBlank { "Sonstige" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (tx.note.isNotBlank()) {
                    Text(
                        tx.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "%02d.%02d.%04d".format(
                        tx.date.dayOfMonth, tx.date.monthNumber, tx.date.year
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tx.vatMode != VatMode.NONE && tx.vatAmount > 0.01) {
                    Text(
                        "Netto ${formatCurrency(tx.netAmount, "EUR")} + " +
                                "USt ${formatCurrency(tx.vatAmount, "EUR")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                formatCurrency(tx.grossAmount, "EUR"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (tx.type == TransactionType.INCOME)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}