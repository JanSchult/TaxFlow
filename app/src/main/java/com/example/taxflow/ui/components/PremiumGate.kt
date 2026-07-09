package com.example.taxflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Zeigt [content], wenn [isPremium] true ist – sonst einen Teaser mit Erklärung
 * und Call-to-Action zur Paywall. So sehen Free-Nutzer, WAS sie verpassen,
 * statt nur eine leere/gesperrte Seite.
 *
 * Verwendung: den kompletten Screen-Inhalt von Dashboard/Übersicht/Fristen/Einstellungen
 * in `content = { ... }` packen.
 */
@Composable
fun PremiumGate(
    isPremium: Boolean,
    title: String,
    description: String,
    onUpgradeClick: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isPremium) {
        content()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(onClick = onUpgradeClick, modifier = Modifier.fillMaxWidth()) {
                Text("Mit Premium freischalten")
            }
        }
    }
}