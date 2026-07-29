package com.example.taxflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taxflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
     fun getAll(): Flow<List<TransactionEntity>>

     @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC, id DESC")
     fun getBetween(from: LocalDate, to: LocalDate): Flow<List<TransactionEntity>>

     @Query("SELECT * FROM transactions WHERE id = :id")
     suspend fun getById(id: Long): TransactionEntity?

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

