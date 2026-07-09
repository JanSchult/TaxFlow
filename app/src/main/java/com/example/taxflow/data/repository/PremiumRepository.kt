package com.example.taxflow.data.repository

import kotlinx.coroutines.flow.StateFlow

interface PremiumRepository {
    /** true = Premium aktiv, false = Free-Tier. Aus lokalem Cache, sofort verfügbar (auch offline). */
    val isPremium: StateFlow<Boolean>

    /** Fragt aktiv beim Play Store nach dem aktuellen Abo-Status und aktualisiert den Cache. */
    suspend fun refreshFromPlayStore()
}

