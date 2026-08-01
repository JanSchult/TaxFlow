import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shared2.domain.model.Transaction
import com.example.taxflow.util.formatCurrency

@Composable
fun ExpandableSummaryCard(
    label: String,
    totalAmount: Double,
    currency: String,
    color: Color,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    items: List<Transaction> // Exakt auf dein Model gematcht
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .clickable { onExpandToggle() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formatCurrency(totalAmount, currency),
                        style = MaterialTheme.typography.titleLarge,
                        color = color
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Einklappen" else "Ausklappen"
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                if (items.isEmpty()) {
                    Text(
                        text = "Keine Einträge in diesem Monat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Zeigt die Notiz an, oder "Buchung #ID" falls leer
                            val displayName = item.note.ifBlank { "Buchung #${item.id}" }

                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatCurrency(item.amount, currency),
                                style = MaterialTheme.typography.bodyMedium,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}