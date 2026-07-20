package com.example.taxflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.taxflow.domain.model.VehicleType

@Composable
 fun MileageCalculatorCard(
    useCalculator: Boolean,
    kilometers: String,
    vehicleType: VehicleType,
    onToggle: (Boolean) -> Unit,
    onKilometersChanged: (String) -> Unit,
    onVehicleTypeChanged: (VehicleType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kilometerpauschale nutzen", style = MaterialTheme.typography.titleMedium)
            Switch(checked = useCalculator, onCheckedChange = onToggle)
        }

        if (useCalculator) {
            Text(
                "Geschäftsfahrten mit dem Privatfahrzeug, Stand 2026 – nicht zu verwechseln mit der Pendlerpauschale für den täglichen Arbeitsweg.",
                style = MaterialTheme.typography.bodyMedium
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                VehicleType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = vehicleType == type,
                        onClick = { onVehicleTypeChanged(type) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = VehicleType.entries.size)
                    ) { Text("${type.label} (${"%.2f".format(type.ratePerKm)} €/km)") }
                }
            }

            OutlinedTextField(
                value = kilometers,
                onValueChange = onKilometersChanged,
                label = { Text("Gefahrene Kilometer") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}