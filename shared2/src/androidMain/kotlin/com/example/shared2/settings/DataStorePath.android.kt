package com.example.shared2.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createAndroidPremiumDataStore(context: Context): DataStore<Preferences> {
    return createPremiumDataStore {
        context.filesDir.resolve(PREMIUM_DATASTORE_FILE_NAME).absolutePath
    }
}
fun createAndroidSettingsDataStore(context: Context): DataStore<Preferences> {
    return createPremiumDataStore {
        context.filesDir.resolve(SETTINGS_DATASTORE_FILE_NAME).absolutePath
    }
}