package com.example.shared2.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.shared2.domain.model.Category
import com.example.shared2.domain.model.TransactionType
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long?,
    val date: LocalDate,
    val note: String,
    val taxDeductiblePercentage: Int = 100   // ← NEU: aus Kategorie beim Erfassen übernommen
)