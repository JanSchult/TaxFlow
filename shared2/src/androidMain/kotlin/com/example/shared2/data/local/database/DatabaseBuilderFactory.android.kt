package com.example.shared2.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

actual class DatabaseBuilderFactory(private val context: Context) {

    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.applicationContext.getDatabasePath("taxflow.db")
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        ).addMigrations(MIGRATION_1_2)
    }
}

/**
 * Migration von Version 1 → 2:
 * TransactionEntity bekommt neue Felder für positionsbasierte USt und EÜR-Kategorisierung.
 * Vorhandene Buchungen werden mit Standardwerten befüllt (grossAmount = bisheriger amount).
 */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN grossAmount REAL NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN netAmount REAL NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN vatAmount REAL NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN vatMode TEXT NOT NULL DEFAULT 'NONE'")
        db.execSQL("ALTER TABLE transactions ADD COLUMN categoryName TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE transactions ADD COLUMN taxDeductiblePercentage INTEGER NOT NULL DEFAULT 100")
        // Bisherige amount-Werte in grossAmount und netAmount übernehmen
        // (alte Buchungen gelten als Kleinunternehmer-Buchungen: brutto = netto, vatAmount = 0)
        db.execSQL("UPDATE transactions SET grossAmount = amount, netAmount = amount")
    }
}