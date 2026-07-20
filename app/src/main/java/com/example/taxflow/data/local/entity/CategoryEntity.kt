package com.example.taxflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taxflow.domain.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val type: TransactionType,
    val colorHex: String,
    val isDefault: Boolean = false,
    val supportsMileageCalculator: Boolean = false
)