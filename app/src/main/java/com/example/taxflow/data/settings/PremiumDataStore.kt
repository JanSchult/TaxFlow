package com.example.taxflow.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.premiumDataStore by preferencesDataStore(name = "taxflow_premium_status")

/**
 * Speichert den zuletzt bekannten Premium-Status lokal, damit gesperrte Funktionen
 * (Dashboard, Übersicht, Fristen, Einstellungen) auch OFFLINE sofort verfügbar sind,
 * ohne bei jedem App-Start erneut den Play Store kontaktieren zu müssen.
 *
 * Wichtig: Das ist ein reiner Komfort-Cache für die UI, KEINE serverseitige Prüfung.
 * Für Produktionsreife später zusätzlich serverseitige Kaufvalidierung ergänzen
 * (siehe Hinweis in MONETIZATION.md).
 */
class PremiumStatusDataStore(private val context: Context) {

    private object Keys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium_active")
    }

    val isPremiumFlow: Flow<Boolean> = context.premiumDataStore.data.map { prefs ->
        prefs[Keys.IS_PREMIUM] ?: false
    }

    suspend fun setPremium(isPremium: Boolean) {
        context.premiumDataStore.edit { prefs ->
            prefs[Keys.IS_PREMIUM] = isPremium
        }
    }
}