package com.example.taxflow.data.notification

import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first

private val Context.reminderDataStore by preferencesDataStore(name = "taxflow_notified_reminders")

/**
 * Merkt sich, welche Fristen-Meilensteine schon benachrichtigt wurden
 * (Schlüssel-Format "{deadlineId}_{daysUntil}", z. B. "3_7"),
 * damit dieselbe Erinnerung nicht mehrfach ausgelöst wird.
 */
class NotifiedRemindersDataStore(private val context: Context) {

    private val key = stringSetPreferencesKey("notified_keys")

    suspend fun getNotifiedKeys(): Set<String> =
        context.reminderDataStore.data.first()[key] ?: emptySet()

    suspend fun markNotified(reminderKey: String) {
        context.reminderDataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = current + reminderKey
        }
    }
}
