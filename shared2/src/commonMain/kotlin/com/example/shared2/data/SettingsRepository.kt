package com.example.shared2.data

import com.example.shared2.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun update(settings: UserSettings)
}