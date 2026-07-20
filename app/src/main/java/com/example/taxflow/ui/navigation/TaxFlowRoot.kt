package com.example.taxflow.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.taxflow.ui.screens.OnboardingScreen
import com.example.taxflow.viewModel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Einstiegspunkt: entscheidet EINMALIG beim App-Start, ob das Onboarding oder
 * direkt die normale App (mit Bottom-Navigation) gezeigt wird.
 * Bewusst AUSSERHALB von TaxFlowNavGraph gehalten, damit das Onboarding ohne
 * Bottom-Bar/Scaffold-Chrome als eigener Vollbild-Flow läuft.
 */
@Composable
fun TaxFlowRoot(viewModel: OnboardingViewModel = koinViewModel()) {
    val isCompleted by viewModel.isCompleted.collectAsState()
    // Lokaler Zwischenzustand, damit nach "Fertig" sofort umgeschaltet wird,
    // ohne auf den DataStore-Schreibvorgang warten zu müssen.
    var forceShowApp by remember { mutableStateOf(false) }

    when {
        isCompleted == null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
        }
        isCompleted == true || forceShowApp -> {
            TaxFlowNavGraph()
        }
        else -> {
            OnboardingScreen(onFinished = {
                viewModel.markCompleted()
                forceShowApp = true
            })
        }
    }
}