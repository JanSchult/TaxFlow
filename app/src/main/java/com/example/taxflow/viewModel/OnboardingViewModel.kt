package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.OnboardingDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val dataStore: OnboardingDataStore
) : ViewModel() {

    /** null = wird noch geladen, true/false = bekannter Zustand. */
    val isCompleted: StateFlow<Boolean?> = dataStore.isCompletedFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun markCompleted() {
        viewModelScope.launch {
            dataStore.markCompleted()
        }
    }
}