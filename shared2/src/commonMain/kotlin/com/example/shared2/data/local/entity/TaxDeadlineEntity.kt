package com.example.shared2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "tax_deadline")
data class TaxDeadlineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDate: LocalDate,
    val note: String = "",
    val isPaid: Boolean = false
)