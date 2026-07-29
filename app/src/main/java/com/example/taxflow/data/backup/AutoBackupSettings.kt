package com.example.taxflow.data.backup

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.autoBackupDataStore by preferencesDataStore(name = "taxflow_auto_backup")

data class AutoBackupSettings(
    val isEnabled: Boolean = false,
    val folderUri: String? = null,
    val lastBackupAt: String? = null
)

/** Speichert, OB automatische Backups aktiv sind, WOHIN (Ordner-URI) und WANN zuletzt gelaufen. */
class AutoBackupDataStore(private val context: Context) {

    private object Keys {
        val ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val FOLDER_URI = stringPreferencesKey("auto_backup_folder_uri")
        val LAST_BACKUP_AT = stringPreferencesKey("auto_backup_last_at")
    }

    val settingsFlow: Flow<AutoBackupSettings> = context.autoBackupDataStore.data.map { prefs ->
        AutoBackupSettings(
            isEnabled = prefs[Keys.ENABLED] ?: false,
            folderUri = prefs[Keys.FOLDER_URI],
            lastBackupAt = prefs[Keys.LAST_BACKUP_AT]
        )
    }

    suspend fun current(): AutoBackupSettings = settingsFlow.first()

    suspend fun setEnabled(enabled: Boolean) {
        context.autoBackupDataStore.edit { it[Keys.ENABLED] = enabled }
    }

    suspend fun setFolderUri(uri: String) {
        context.autoBackupDataStore.edit { it[Keys.FOLDER_URI] = uri }
    }

    suspend fun setLastBackupAt(isoDateTime: String) {
        context.autoBackupDataStore.edit { it[Keys.LAST_BACKUP_AT] = isoDateTime }
    }
}