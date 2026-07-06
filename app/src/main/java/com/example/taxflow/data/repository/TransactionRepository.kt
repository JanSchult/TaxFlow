package com.example.taxflow.data.repository

import com.example.taxflow.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getBetween(from: LocalDate, to: LocalDate): Flow<List<Transaction>>
    suspend fun add(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
}