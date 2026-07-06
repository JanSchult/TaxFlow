package com.example.taxflow.domain.model

data class Category(
    val id: Long = 0L,
    val name: String,
    val type: TransactionType,
    val colorHex: String = "#4C6EF5",
    val isDeleted: Boolean = false
)