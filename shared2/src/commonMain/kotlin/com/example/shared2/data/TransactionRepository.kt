package com.example.shared2.data

import com.example.shared2.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getBetween(from:LocalDate, to: LocalDate): Flow<List<Transaction>>
    suspend fun add(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
}