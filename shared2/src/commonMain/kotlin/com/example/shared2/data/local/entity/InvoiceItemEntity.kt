package com.example.shared2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared2.domain.model.VatMode

@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val description: String,
    val quantity: Double,
    val unitPriceNet: Double,
    val vatMode: VatMode
)