package com.example.taxflow.data.local

import com.example.shared2.domain.model.TransactionType
import com.example.taxflow.data.local.entity.CategoryEntity
object DefaultCategories {

    const val MILEAGE_CATEGORY_NAME = "Fahrtkosten & Reisen"

    val all: List<CategoryEntity> = listOf(
        // Einnahmen
        CategoryEntity(name = "Honorar", type = TransactionType.INCOME, colorHex = "#2F9E44", isDefault = true),
        CategoryEntity(name = "Sonstige Einnahmen", type = TransactionType.INCOME, colorHex = "#37B24D", isDefault = true),

        // Ausgaben
        CategoryEntity(name = "Software & Abos", type = TransactionType.EXPENSE, colorHex = "#E8590C", isDefault = true),
        CategoryEntity(name = "Hardware & Ausstattung", type = TransactionType.EXPENSE, colorHex = "#D9480F", isDefault = true),
        CategoryEntity(name = "Miete & Nebenkosten", type = TransactionType.EXPENSE, colorHex = "#E67700", isDefault = true),
        CategoryEntity(
            name = MILEAGE_CATEGORY_NAME,
            type = TransactionType.EXPENSE,
            colorHex = "#A61E4D",
            isDefault = true,
            supportsMileageCalculator = true
        ),
        CategoryEntity(name = "Versicherungen", type = TransactionType.EXPENSE, colorHex = "#C92A2A", isDefault = true),
        CategoryEntity(name = "Fortbildung & Fachliteratur", type = TransactionType.EXPENSE, colorHex = "#5C940D", isDefault = true),
        CategoryEntity(name = "Marketing & Werbung", type = TransactionType.EXPENSE, colorHex = "#F08C00", isDefault = true),
        CategoryEntity(name = "Telefon & Internet", type = TransactionType.EXPENSE, colorHex = "#1971C2", isDefault = true),
        CategoryEntity(name = "Bank- & Kontogebühren", type = TransactionType.EXPENSE, colorHex = "#495057", isDefault = true),
        CategoryEntity(name = "Steuerberatung & Buchhaltung", type = TransactionType.EXPENSE, colorHex = "#5F3DC4", isDefault = true),
        CategoryEntity(name = "Bewirtung", type = TransactionType.EXPENSE, colorHex = "#AE3EC9", isDefault = true),
        CategoryEntity(name = "Sonstige Ausgaben", type = TransactionType.EXPENSE, colorHex = "#862E9C", isDefault = true)
    )
}
