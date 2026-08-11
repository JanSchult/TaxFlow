package com.example.shared2.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.shared2.data.local.dao.CategoryDao
import com.example.shared2.data.local.dao.TaxDeadlineDao
import com.example.shared2.data.local.dao.TransactionDao
import com.example.shared2.data.local.entity.CategoryEntity
import com.example.shared2.data.local.entity.TaxDeadlineEntity
import com.example.shared2.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, TaxDeadlineEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taxDeadlineDao(): TaxDeadlineDao
}

/**
 * NUR die "expect"-Deklaration hier - die "actual"-Implementierung generiert
 * der Room-KSP-Compiler automatisch pro Plattform. Die Suppress-Annotationen
 * sind normal und nötig (IntelliJ meldet sonst fälschlich "no actual found",
 * weil es den generierten Code zur Bearbeitungszeit noch nicht sieht).
 */
@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}