package com.example.taxflow.data.repository

import com.example.taxflow.domain.model.TaxDeadline
import kotlinx.coroutines.flow.Flow

interface TaxDeadlineRepository {
    fun getAll(): Flow<List<TaxDeadline>>
    suspend fun add(deadline: TaxDeadline): Long
    suspend fun update(deadline: TaxDeadline)
    suspend fun delete(deadline: TaxDeadline)
}