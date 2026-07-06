package com.example.taxflow.data.repository

import com.example.taxflow.data.local.dao.TransactionDao
import com.example.taxflow.data.local.entity.TransactionEntity
import com.example.taxflow.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAll(): Flow<List<Transaction>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getBetween(from: LocalDate, to: LocalDate): Flow<List<Transaction>> =
        dao.getBetween(from, to).map { list -> list.map { it.toDomain() } }

    override suspend fun add(transaction: Transaction): Long =
        dao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) =
        dao.update(transaction.toEntity())

    override suspend fun delete(transaction: Transaction) =
        dao.delete(transaction.toEntity())
}

private fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId ?: 0L,
    date = date,
    note = note
)

private fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId,
    date = date,
    note = note
)