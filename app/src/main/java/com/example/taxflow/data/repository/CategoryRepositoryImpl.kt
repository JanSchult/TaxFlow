package com.example.taxflow.data.repository

import com.example.taxflow.data.local.dao.CategoryDao
import com.example.taxflow.data.local.entity.CategoryEntity
import com.example.taxflow.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(category: Category): Long =
        dao.insert(category.toEntity())
}

private fun CategoryEntity.toDomain() = Category(
    id = id, name = name, type = type, colorHex = colorHex
)

private fun Category.toEntity() = CategoryEntity(
    id = id, name = name, type = type, colorHex = colorHex
)