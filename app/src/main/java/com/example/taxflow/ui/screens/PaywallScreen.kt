package com.example.taxflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.taxflow.ui.components.PlanCard
import com.example.taxflow.viewModel.PaywallViewModel
import com.example.taxflow.viewModel.SubscriptionPlan
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    onPurchaseSuccessful: () -> Unit,
    viewModel: PaywallViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity

    var selectedPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }

    LaunchedEffect(state.purchaseSuccessful) {
        if (state.purchaseSuccessful) onPurchaseSuccessful()
    }
    LaunchedEffect(state.monthlyPlan, state.yearlyPlan) {
        // Standardmäßig das Jahresabo vorauswählen (meist bester Wert für den Nutzer).
        if (selectedPlan == null) selectedPlan = state.yearlyPlan ?: state.monthlyPlan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Schließen")
            }
        }

        Text("TaxFlow Premium", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Behalte deine Steuerrücklage, Fristen und Jahresübersicht im Griff.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        listOf(
            "Automatische Steuerrücklagen-Berechnung",
            "Monats- & Jahresübersicht",
            "Fristen & Zahlungserinnerungen",
            "Individuelle Steuerquote & Sparziel"
        ).forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.padding(start = 8.dp))
                Text(feature, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(24.dp))

        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
            state.errorMessage != null -> Text(
                state.errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error
            )
            else -> {
                state.yearlyPlan?.let { plan ->
                    PlanCard(
                        plan = plan,
                        title = "Jährlich",
                        badge = "Bestes Angebot",
                        isSelected = selectedPlan?.basePlanId == plan.basePlanId,
                        onClick = { selectedPlan = plan }
                    )
                }
                Spacer(Modifier.height(12.dp))
                state.monthlyPlan?.let { plan ->
                    PlanCard(
                        plan = plan,
                        title = "Monatlich",
                        badge = null,
                        isSelected = selectedPlan?.basePlanId == plan.basePlanId,
                        onClick = { selectedPlan = plan }
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val plan = selectedPlan
                        if (plan != null && activity != null) {
                            viewModel.purchase(activity, plan)
                        }
                    },
                    enabled = selectedPlan != null && activity != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Jetzt Premium freischalten")
                }

                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Käufe wiederherstellen")
                }

                Text(
                    "Abo verlängert sich automatisch. Jederzeit über Google Play kündbar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
