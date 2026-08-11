package com.example.shared2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shared2.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(category: List<CategoryEntity>)
    @Insert
    suspend fun insert(category: CategoryEntity) : Long
    @Delete
    suspend fun delete(category: CategoryEntity)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): Flow<List<CategoryEntity>>
    @Query("SELECT COUNT (*) FROM categories")
    suspend fun count(): Int

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}