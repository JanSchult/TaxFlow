package com.example.shared2.data.repository

import com.example.shared2.domain.model.UserSettings
import com.example.shared2.settings.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override val settings: Flow<UserSettings> = dataStore.settingsFlow
    override suspend fun update(settings: UserSettings) = dataStore.update(settings)
}