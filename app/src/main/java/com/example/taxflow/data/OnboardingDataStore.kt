package com.example.taxflow.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore by preferencesDataStore(name = "taxflow_onboarding")

/** Merkt sich dauerhaft, ob der Nutzer das Onboarding schon durchlaufen hat. */
class OnboardingDataStore(private val context: Context) {

    private val key = booleanPreferencesKey("onboarding_completed")

    val isCompletedFlow: Flow<Boolean> = context.onboardingDataStore.data.map { prefs ->
        prefs[key] ?: false
    }

    suspend fun markCompleted() {
        context.onboardingDataStore.edit { prefs -> prefs[key] = true }
    }
}