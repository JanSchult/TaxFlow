package com.example.shared2.data.repository

import com.example.shared2.domain.model.TaxDeadline
import kotlinx.coroutines.flow.Flow

interface TaxDeadlineRepository {
    fun getAll(): Flow<List<TaxDeadline>>
    suspend fun add(deadline: TaxDeadline): Long
    suspend fun update(deadline: TaxDeadline)
    suspend fun delete(deadline: TaxDeadline)
}