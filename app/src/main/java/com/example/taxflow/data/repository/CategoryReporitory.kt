package com.example.taxflow.data.repository

import com.example.taxflow.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAll(): Flow<List<Category>>
    suspend fun add(category: Category): Long
}
