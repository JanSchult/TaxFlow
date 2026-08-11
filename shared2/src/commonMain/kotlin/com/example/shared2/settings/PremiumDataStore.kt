package com.example.shared2.settings


/**
 * Speichert den zuletzt bekannten Premium-Status lokal, damit gesperrte Funktionen
 * (Dashboard, Übersicht, Fristen, Einstellungen) auch OFFLINE sofort verfügbar sind,
 * ohne bei jedem App-Start erneut den Play Store kontaktieren zu müssen.
 *
 * Wichtig: Das ist ein reiner Komfort-Cache für die UI, KEINE serverseitige Prüfung.
 * Für Produktionsreife später zusätzlich serverseitige Kaufvalidierung ergänzen
 * (siehe Hinweis in MONETIZATION.md).
 */
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow


class PremiumStatusDataStore(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium_active")
    }

    val isPremiumFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_PREMIUM] ?: false
    }

    suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_PREMIUM] = isPremium
        }
    }
}