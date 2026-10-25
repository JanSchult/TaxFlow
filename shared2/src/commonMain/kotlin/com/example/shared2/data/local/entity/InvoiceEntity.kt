package com.example.shared2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared2.domain.model.InvoiceStatus
import kotlinx.datetime.LocalDate

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String, // e.g. RE-2026-001
    val customerId: Long,
    val issueDate: LocalDate,
    val dueDate: LocalDate,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val grossAmount: Double,
    val netAmount: Double,
    val vatAmount: Double
)