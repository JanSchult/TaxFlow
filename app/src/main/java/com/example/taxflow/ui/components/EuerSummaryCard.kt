package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.shared2.domain.model.EuerReport
import com.example.shared2.domain.model.VatMode

@Composable
fun EuerSummaryCard(report: EuerReport) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Zusammenfassung", style = MaterialTheme.typography.titleMedium)
            val isKlein = report.vatMode == VatMode.NONE

            if (isKlein) {
                EuerRow("Betriebseinnahmen (§ 19 UStG, Zeile 12)", report.incomeKleinunternehmer)
            } else {
                EuerRow("Bruttoeinnahmen", report.incomeGross)
                EuerRow("davon Netto (Zeile 14 EÜR)", report.incomeNet)
                EuerRow("davon vereinnahmte USt (Zeile 16 EÜR)", report.incomeVat,
                    color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            EuerRow("Gesamtausgaben (brutto)", report.totalExpenses)
            EuerRow("davon steuerlich abziehbar", report.totalDeductible,
                color = MaterialTheme.colorScheme.secondary)
            if (report.totalNonDeductible > 0.01) {
                EuerRow("davon NICHT abziehbar (z. B. Bewirtung 30 %, Bußgelder)",
                    report.totalNonDeductible, color = MaterialTheme.colorScheme.error)
            }
            if (report.expenseVat > 0.01) {
                EuerRow("Gezahlte Vorsteuer (Zeile 59 EÜR)", report.expenseVat)
            }
        }
    }
}
