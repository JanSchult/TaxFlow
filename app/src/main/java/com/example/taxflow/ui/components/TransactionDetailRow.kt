package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.Transaction
import com.example.shared2.domain.model.VatMode
import com.example.taxflow.util.formatDate
import com.example.taxflow.util.formatEur

@Composable
fun TransactionDetailRow(tx: Transaction) {
    val isRegel = tx.vatMode != VatMode.NONE
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.note.ifBlank { tx.categoryName },
                style = MaterialTheme.typography.bodyMedium)
            Text(formatDate(tx.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (isRegel) {
                Text("Netto ${formatEur(tx.netAmount)} + " +
                        "USt ${tx.vatMode.ratePercent?.toInt()} % ${formatEur(tx.vatAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Text(formatEur(tx.grossAmount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium)
    }
}
