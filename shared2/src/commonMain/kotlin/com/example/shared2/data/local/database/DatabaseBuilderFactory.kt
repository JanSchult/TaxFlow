package com.example.shared2.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Android braucht einen Context, iOS einen Dateipfad - deshalb eine "expect class"
 * statt einer einfachen "expect fun". Wird jeweils im androidMain/iosMain mit dem
 * plattformspezifischen Konstruktor-Parameter erzeugt.
 */
expect class DatabaseBuilderFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}

fun getRoomDatabase(factory: DatabaseBuilderFactory): AppDatabase {
    return factory.create()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

/** Einmalig nach dem Erstellen aufrufen, damit Standard-Kategorien vorhanden sind. */
suspend fun AppDatabase.seedDefaultCategoriesIfNeeded() {
    if (categoryDao().count() == 0) {
        categoryDao().insertAll(DefaultCategories.all)
    }
}