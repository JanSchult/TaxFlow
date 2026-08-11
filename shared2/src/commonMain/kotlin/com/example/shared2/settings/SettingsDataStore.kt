package com.example.shared2.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.domain.model.VatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val TAX_RATE = doublePreferencesKey("tax_rate_percent")
        val CURRENCY = stringPreferencesKey("currency_code")
        val SAVINGS_GOAL = doublePreferencesKey("monthly_savings_goal")
        val BUFFER = doublePreferencesKey("buffer_percent")
        val VAT_MODE = stringPreferencesKey("vat_mode")
    }

    val settingsFlow: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            taxRatePercent = prefs[Keys.TAX_RATE] ?: 30.0,
            currencyCode = prefs[Keys.CURRENCY] ?: "EUR",
            monthlySavingsGoal = prefs[Keys.SAVINGS_GOAL] ?: 0.0,
            bufferPercent = prefs[Keys.BUFFER] ?: 10.0,
            vatMode = prefs[Keys.VAT_MODE]?.let { runCatching { VatMode.valueOf(it) }.getOrNull() }
                ?: VatMode.NONE
        )
    }

    suspend fun update(settings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.TAX_RATE] = settings.taxRatePercent
            prefs[Keys.CURRENCY] = settings.currencyCode
            prefs[Keys.SAVINGS_GOAL] = settings.monthlySavingsGoal
            prefs[Keys.BUFFER] = settings.bufferPercent
            prefs[Keys.VAT_MODE] = settings.vatMode.name
        }
    }
}