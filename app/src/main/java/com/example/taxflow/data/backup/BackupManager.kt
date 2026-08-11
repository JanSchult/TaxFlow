package com.example.taxflow.data.backup

import androidx.room.withTransaction
import com.example.shared2.data.repository.SettingsRepository
import com.example.shared2.domain.model.TransactionType
import com.example.shared2.domain.model.UserSettings
import com.example.shared2.data.local.dao.CategoryDao
import com.example.shared2.data.local.dao.TaxDeadlineDao
import com.example.shared2.data.local.dao.TransactionDao
import com.example.shared2.data.local.database.AppDatabase
import com.example.shared2.data.local.entity.CategoryEntity
import com.example.shared2.data.local.entity.TaxDeadlineEntity
import com.example.shared2.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val SCHEMA_VERSION = 1

/**
 * Exportiert/importiert den kompletten lokalen Datenbestand (Buchungen, Kategorien,
 * Fristen, Einstellungen) als eine einzelne JSON-Datei. Bewusst mit org.json
 * (Android-Bordmittel) statt einer Serialisierungs-Bibliothek umgesetzt - kein
 * neuer Gradle-Dependency, kein zusätzliches Versionsrisiko.
 *
 * Wichtig: restoreFromJson() ERSETZT alle vorhandenen Daten vollständig.
 * Die Bestätigung dafür muss auf UI-Ebene eingeholt werden, bevor das aufgerufen wird.
 */
class BackupManager(
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val deadlineDao: TaxDeadlineDao,
    private val settingsRepository: SettingsRepository
) {

    suspend fun exportToJson(): String {
        val categories = categoryDao.getAll().first()
        val transactions = transactionDao.getAll().first()
        val deadlines = deadlineDao.getAll().first()
        val settings = settingsRepository.settings.first()

        val root = JSONObject()
        root.put("schemaVersion", SCHEMA_VERSION)
        root.put("exportedAt", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString())
        root.put("settings", JSONObject().apply {
            put("taxRatePercent", settings.taxRatePercent)
            put("currencyCode", settings.currencyCode)
            put("monthlySavingsGoal", settings.monthlySavingsGoal)
            put("bufferPercent", settings.bufferPercent)
        })

        root.put("categories", JSONArray().apply {
            categories.forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("type", c.type.name)
                    put("colorHex", c.colorHex)
                    put("isDefault", c.isDefault)
                    put("taxDeductiblePercentage", c.taxDeductiblePercentage)
                    put("supportsMileageCalculator", c.supportsMileageCalculator)
                })
            }
        })

        root.put("transactions", JSONArray().apply {
            transactions.forEach { t ->
                put(JSONObject().apply {
                    put("id", t.id)
                    put("amount", t.amount)
                    put("type", t.type.name)
                    put("categoryId", t.categoryId ?: JSONObject.NULL)
                    put("date", t.date.toString())
                    put("note", t.note)
                })
            }
        })

        root.put("deadlines", JSONArray().apply {
            deadlines.forEach { d ->
                put(JSONObject().apply {
                    put("id", d.id)
                    put("title", d.title)
                    put("dueDate", d.dueDate.toString())
                    put("note", d.note)
                    put("isPaid", d.isPaid)
                })
            }
        })

        return root.toString(2)
    }

    suspend fun restoreFromJson(json: String) {
        val root = JSONObject(json)

        val settingsJson = root.getJSONObject("settings")
        val categoriesJson = root.getJSONArray("categories")
        val transactionsJson = root.getJSONArray("transactions")
        val deadlinesJson = root.optJSONArray("deadlines") ?: JSONArray()

        database.withTransaction {
            transactionDao.deleteAll()
            categoryDao.deleteAll()
            deadlineDao.deleteAll()

            for (i in 0 until categoriesJson.length()) {
                val c = categoriesJson.getJSONObject(i)
                categoryDao.insert(
                    CategoryEntity(
                        id = c.getLong("id"),
                        name = c.getString("name"),
                        type = TransactionType.valueOf(c.getString("type")),
                        colorHex = c.getString("colorHex"),
                        isDefault = c.optBoolean("isDefault", false),
                        taxDeductiblePercentage = c.optInt("taxDeductiblePercentage", 100) ,
                        supportsMileageCalculator = c.optBoolean("supportsMileageCalculator", false)
                    )
                )
            }

            for (i in 0 until transactionsJson.length()) {
                val t = transactionsJson.getJSONObject(i)
                transactionDao.insert(
                    TransactionEntity(
                        id = t.getLong("id"),
                        amount = t.getDouble("amount"),
                        type = TransactionType.valueOf(t.getString("type")),
                        categoryId = if (t.isNull("categoryId")) null else t.getLong("categoryId"),
                        date = LocalDate.parse(t.getString("date")),
                        note = t.optString("note", "")
                    )
                )
            }

            for (i in 0 until deadlinesJson.length()) {
                val d = deadlinesJson.getJSONObject(i)
                deadlineDao.insert(
                    TaxDeadlineEntity(
                        id = d.getLong("id"),
                        title = d.getString("title"),
                        dueDate = LocalDate.parse(d.getString("dueDate")),
                        note = d.optString("note", ""),
                        isPaid = d.optBoolean("isPaid", false)
                    )
                )
            }
        }

        // Settings liegen in DataStore, nicht in Room - bewusst außerhalb der DB-Transaktion.
        settingsRepository.update(
            UserSettings(
                taxRatePercent = settingsJson.optDouble("taxRatePercent", 30.0),
                currencyCode = settingsJson.optString("currencyCode", "EUR"),
                monthlySavingsGoal = settingsJson.optDouble("monthlySavingsGoal", 0.0),
                bufferPercent = settingsJson.optDouble("bufferPercent", 10.0)
            )
        )
    }
}
