package com.example.shared2.data.repository

import com.example.shared2.data.local.dao.TransactionDao
import com.example.shared2.data.local.entity.TransactionEntity
import com.example.shared2.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAll(): Flow<List<Transaction>> =
        dao.getAll().map { it.map { e -> e.toDomain() } }

    override fun getBetween(from: LocalDate, to: LocalDate): Flow<List<Transaction>> =
        dao.getBetween(from, to).map { it.map { e -> e.toDomain() } }

    override suspend fun add(transaction: Transaction): Long =
        dao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) =
        dao.update(transaction.toEntity())

    override suspend fun delete(transaction: Transaction) =
        dao.delete(transaction.toEntity())
}

// taxDeductiblePercentage wird 1:1 vom Entity ins Domain-Modell übernommen
private fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId ?: 0L,
    date = date,
    note = note,
    taxDeductiblePercentage = taxDeductiblePercentage
)

private fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId,
    date = date,
    note = note,
    taxDeductiblePercentage = taxDeductiblePercentage
)