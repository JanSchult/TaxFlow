package com.example.taxflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.taxflow.data.local.entity.TaxDeadlineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDeadlineDao {
    @Insert
    suspend fun insert(deadline: TaxDeadlineEntity): Long
    @Update
    suspend fun update(deadline: TaxDeadlineEntity)
    @Delete
    suspend fun delete(deadline: TaxDeadlineEntity)
    @Query("SELECT * FROM tax_deadline ORDER BY dueDate ASC")
    fun getAll(): Flow<List<TaxDeadlineEntity>>
}
