package com.example.shared2.data.repository

import com.example.shared2.data.local.dao.CategoryDao
import com.example.shared2.data.local.entity.CategoryEntity
import com.example.shared2.domain.model.Category
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
    id = id,
    name = name,
    type = type,
    colorHex = colorHex,
    isDefault = isDefault,
    taxDeductiblePercentage = taxDeductiblePercentage,
    supportsMileageCalculator = supportsMileageCalculator
)

private fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    type = type,
    colorHex = colorHex,
    isDefault = isDefault,
    taxDeductiblePercentage = taxDeductiblePercentage,
    supportsMileageCalculator = supportsMileageCalculator
)