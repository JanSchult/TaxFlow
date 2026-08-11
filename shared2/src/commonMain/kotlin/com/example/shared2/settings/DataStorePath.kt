package com.example.shared2.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

// Erstellt den DataStore plattformübergreifend
fun createPremiumDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val PREMIUM_DATASTORE_FILE_NAME = "taxflow_premium_status.preferences_pb"

internal const val SETTINGS_DATASTORE_FILE_NAME = "taxflow_settings.preferences_pb"