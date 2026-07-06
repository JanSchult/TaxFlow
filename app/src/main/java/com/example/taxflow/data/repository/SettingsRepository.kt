package com.example.taxflow.data.repository

import com.example.taxflow.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun update(settings: UserSettings)
}

