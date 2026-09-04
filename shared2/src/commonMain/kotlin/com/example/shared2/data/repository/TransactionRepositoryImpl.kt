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
fun TransactionEntity.toDomain(categoryName: String = "") = Transaction(
    id = id,
    grossAmount = grossAmount,
    netAmount = netAmount,
    vatAmount = vatAmount,
    vatMode = vatMode,
    type = type,
    categoryId = categoryId ?: 0L,
    categoryName = categoryName,
    date = date,
    note = note,
    taxDeductiblePercentage = taxDeductiblePercentage
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    grossAmount = grossAmount,
    netAmount = netAmount,
    vatAmount = vatAmount,
    vatMode = vatMode,
    type = type,
    categoryId = if (categoryId == 0L) null else categoryId,
    date = date,
    note = note,
    taxDeductiblePercentage = taxDeductiblePercentage
)