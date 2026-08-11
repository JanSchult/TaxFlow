package com.example.shared2.data.local.database

import com.example.shared2.data.local.entity.CategoryEntity
import com.example.shared2.domain.model.TransactionType


object DefaultCategories {

    const val MILEAGE_CATEGORY_NAME = "Kraftfahrzeug- & Fahrtkosten"

    val all: List<CategoryEntity> = listOf(
        // ==========================================
        // BETRIEBSEINNAHMEN (Anlage EÜR)
        // ==========================================
        CategoryEntity(
            name = "Umsatzerlöse / Honorare",
            type = TransactionType.INCOME,
            colorHex = "#2F9E44",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Vereinnahmte Umsatzsteuer",
            type = TransactionType.INCOME,
            colorHex = "#37B24D",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Vom Finanzamt erstattete USt",
            type = TransactionType.INCOME,
            colorHex = "#40C057",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Sachentnahmen (z.B. PKW-Nutzung)",
            type = TransactionType.INCOME,
            colorHex = "#51CF66",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Sonstige Betriebseinnahmen",
            type = TransactionType.INCOME,
            colorHex = "#69DB7C",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),

        // ==========================================
        // BETRIEBSAUSGABEN (Anlage EÜR)
        // ==========================================
        CategoryEntity(
            name = "Wareneinkauf & Fremdleistungen",
            type = TransactionType.EXPENSE,
            colorHex = "#D9480F",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Miete & Raumkosten (exkl. Arbeitszimmer)",
            type = TransactionType.EXPENSE,
            colorHex = "#E67700",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Häusliches Arbeitszimmer / Homeoffice-Pauschale",
            type = TransactionType.EXPENSE,
            colorHex = "#F08C00",
            isDefault = true,
            taxDeductiblePercentage = 100 // 100% der zulässigen Pauschale/Kosten
        ),
        CategoryEntity(
            name = "Software, IT & Lizenzen",
            type = TransactionType.EXPENSE,
            colorHex = "#E8590C",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Büromaterial & Fachliteratur",
            type = TransactionType.EXPENSE,
            colorHex = "#5C940D",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Telefon, Internet & Porto",
            type = TransactionType.EXPENSE,
            colorHex = "#1971C2",
            isDefault = true,
            taxDeductiblePercentage = 100 // Bzw. geschäftlicher Anteil
        ),
        CategoryEntity(
            name = MILEAGE_CATEGORY_NAME,
            type = TransactionType.EXPENSE,
            colorHex = "#A61E4D",
            isDefault = true,
            supportsMileageCalculator = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Übernachtungs- & Reisenebenkosten",
            type = TransactionType.EXPENSE,
            colorHex = "#C2255C",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Bewirtungskosten (geschäftlich)",
            type = TransactionType.EXPENSE,
            colorHex = "#AE3EC9",
            isDefault = true,
            taxDeductiblePercentage = 70 // Gesetzlich genau 70% abziehbar
        ),
        CategoryEntity(
            name = "Geschenke an Kunden (bis 50€ / Person)",
            type = TransactionType.EXPENSE,
            colorHex = "#D0B200",
            isDefault = true,
            taxDeductiblePercentage = 100 // 100% sofern unter der Freigrenze
        ),
        CategoryEntity(
            name = "Werbekosten & Repräsentation",
            type = TransactionType.EXPENSE,
            colorHex = "#F59F00",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Fortbildung & Seminare",
            type = TransactionType.EXPENSE,
            colorHex = "#74B816",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Rechts- & Steuerberatung, Buchführung",
            type = TransactionType.EXPENSE,
            colorHex = "#5F3DC4",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Beiträge, Gebühren & Versicherungen",
            type = TransactionType.EXPENSE,
            colorHex = "#C92A2A",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Bankspesen & Zinsen",
            type = TransactionType.EXPENSE,
            colorHex = "#495057",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Gezahlte Vorsteuer",
            type = TransactionType.EXPENSE,
            colorHex = "#3B5EDB",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "An das Finanzamt gezahlte USt",
            type = TransactionType.EXPENSE,
            colorHex = "#364FC7",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Geringwertige Wirtschaftsgüter (GWG)",
            type = TransactionType.EXPENSE,
            colorHex = "#862E9C",
            isDefault = true,
            taxDeductiblePercentage = 100
        ),
        CategoryEntity(
            name = "Geldbußen & Ordnungswidrigkeiten",
            type = TransactionType.EXPENSE,
            colorHex = "#212529",
            isDefault = true,
            taxDeductiblePercentage = 0 // Gesetzlich nicht abziehbar (§ 4 Abs. 5 EStG)
        ),
        CategoryEntity(
            name = "Sonstige Betriebsausgaben",
            type = TransactionType.EXPENSE,
            colorHex = "#868E96",
            isDefault = true,
            taxDeductiblePercentage = 100
        )
    )
}