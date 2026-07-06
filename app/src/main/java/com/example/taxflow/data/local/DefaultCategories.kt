package com.example.taxflow.data.local

import com.example.taxflow.data.local.entity.CategoryEntity
import com.example.taxflow.domain.model.TransactionType

object DefaultCategories {
    val all: List<CategoryEntity> =listOf(
        CategoryEntity(name = "Honorar", type = TransactionType.INCOME, colorHex = "#2F9E44", isDefault = true),
        CategoryEntity(name = "Sonstige Einnahmen", type = TransactionType.INCOME, colorHex = "#37B24D", isDefault = true),
        CategoryEntity(name = "Software & Tools", type = TransactionType.EXPENSE, colorHex = "#E8590C", isDefault = true),
        CategoryEntity(name = "Büro & Miete ", type = TransactionType.EXPENSE, colorHex = "#E67700", isDefault = true),
        CategoryEntity(name = "Versicherung", type = TransactionType.EXPENSE, colorHex = "#C92A2A", isDefault = true),
        CategoryEntity(name = "Fahrtkosten", type = TransactionType.EXPENSE, colorHex = "#A61E4D", isDefault = true),
        CategoryEntity(name = "Sonstige Ausgaben", type = TransactionType.EXPENSE, colorHex = "#862E9C", isDefault = true)
    )
}