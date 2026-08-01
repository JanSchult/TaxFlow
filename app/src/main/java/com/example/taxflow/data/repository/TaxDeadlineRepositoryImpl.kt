package com.example.taxflow.data.repository

import com.example.shared2.data.TaxDeadlineRepository
import com.example.shared2.domain.model.TaxDeadline
import com.example.taxflow.data.local.dao.TaxDeadlineDao
import com.example.taxflow.data.local.entity.TaxDeadlineEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaxDeadlineRepositoryImpl(
    private val dao: TaxDeadlineDao
) : TaxDeadlineRepository {

    override fun getAll(): Flow<List<TaxDeadline>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(deadline: TaxDeadline): Long = dao.insert(deadline.toEntity())
    override suspend fun update(deadline: TaxDeadline) = dao.update(deadline.toEntity())
    override suspend fun delete(deadline: TaxDeadline) = dao.delete(deadline.toEntity())
}

private fun TaxDeadlineEntity.toDomain() = TaxDeadline(id, title, dueDate, note, isPaid)
private fun TaxDeadline.toEntity() = TaxDeadlineEntity(id, title, dueDate, note, isPaid)