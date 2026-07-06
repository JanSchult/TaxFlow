package com.example.taxflow.data.settings

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taxflow.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "taxflow_settings")

/**
 * Speichert die persönlichen Einstellungen (Steuerquote, Währung, Sparziel, Puffer)
 * dauerhaft und offline über Jetpack DataStore.
 */
class SettingsDataStore(private val context: Context) {

    private object Keys {
        val TAX_RATE = doublePreferencesKey("tax_rate_percent")
        val CURRENCY = stringPreferencesKey("currency_code")
        val SAVINGS_GOAL = doublePreferencesKey("monthly_savings_goal")
        val BUFFER = doublePreferencesKey("buffer_percent")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            taxRatePercent = prefs[Keys.TAX_RATE] ?: 30.0,
            currencyCode = prefs[Keys.CURRENCY] ?: "EUR",
            monthlySavingsGoal = prefs[Keys.SAVINGS_GOAL] ?: 0.0,
            bufferPercent = prefs[Keys.BUFFER] ?: 10.0
        )
    }

    suspend fun update(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TAX_RATE] = settings.taxRatePercent
            prefs[Keys.CURRENCY] = settings.currencyCode
            prefs[Keys.SAVINGS_GOAL] = settings.monthlySavingsGoal
            prefs[Keys.BUFFER] = settings.bufferPercent
        }
    }
}