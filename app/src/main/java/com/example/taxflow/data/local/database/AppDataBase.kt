package com.example.taxflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taxflow.data.local.dao.CategoryDao
import com.example.taxflow.data.local.dao.TaxDeadlineDao
import com.example.taxflow.data.local.dao.TransactionDao
import com.example.taxflow.data.local.entity.TransactionEntity
import com.example.taxflow.data.local.entity.CategoryEntity
import com.example.taxflow.data.local.entity.TaxDeadlineEntity
import androidx.room.Room
import com.example.taxflow.data.local.DefaultCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, TaxDeadlineEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taxDeadlineDao(): TaxDeadlineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taxflow.db"
                )
                    .addCallback(SeedCallback(context))
                    .build().also { INSTANCE = it }
            }
    }

    /**
     * Befüllt die Datenbank beim allerersten Start mit Standard-Kategorien,
     * damit die App sofort nutzbar ist.
     */
    private class SeedCallback(private val context: Context) : Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                getInstance(context).categoryDao().insertAll(DefaultCategories.all)
            }
        }
    }
}