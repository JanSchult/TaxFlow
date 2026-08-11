package com.example.shared2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared2.domain.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val type: TransactionType,
    val colorHex: String,
    val isDefault: Boolean = false,
    val supportsMileageCalculator: Boolean = false,
    val taxDeductiblePercentage: Int = 100   // 0–100: steuerlich abziehbarer Anteil
)