package com.example.shared2.data

import com.example.shared2.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAll(): Flow<List<Category>>
    suspend fun add(category: Category): Long
}
