package com.example.shared2.data.local.database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilderFactory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val documentDirectory = requireNotNull(
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
        ) { "Dokumenten-Verzeichnis konnte nicht ermittelt werden" }

        val dbFilePath = documentDirectory.path + "/taxflow.db"
        return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
    }
}