package com.example.taxflow.data.backup

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

/**
 * Wöchentlicher Hintergrund-Job: legt automatisch eine neue Backup-Datei im
 * vom Nutzer einmalig gewählten Ordner ab (siehe AutoBackupDataStore) und
 * räumt alte automatische Backups auf (nur die letzten [RETENTION_COUNT] behalten).
 *
 * No-op, wenn automatisches Backup nicht aktiviert ist oder kein Ordner gewählt wurde.
 */
class AutoBackupWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val backupManager: BackupManager by inject()
    private val autoBackupDataStore: AutoBackupDataStore by inject()

    companion object {
        const val UNIQUE_WORK_NAME = "auto_backup_check"
        const val FILE_PREFIX = "taxflow_autobackup_"
        const val RETENTION_COUNT = 5
    }

    override suspend fun doWork(): Result {
        return try {
            val settings = autoBackupDataStore.current()
            val folderUriString = settings.folderUri

            if (!settings.isEnabled || folderUriString.isNullOrBlank()) {
                return Result.success() // Nichts zu tun, kein Fehler.
            }

            val treeUri = folderUriString.toUri()
            val folder = DocumentFile.fromTreeUri(applicationContext, treeUri)
                ?: return Result.failure()

            val json = backupManager.exportToJson()
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
            val newFile = folder.createFile("application/json", "$FILE_PREFIX$timestamp.json")
                ?: return Result.retry()

            applicationContext.contentResolver.openOutputStream(newFile.uri)?.use {
                it.write(json.toByteArray())
            }

            cleanupOldBackups(folder)
            autoBackupDataStore.setLastBackupAt(LocalDateTime.now().toString())

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun cleanupOldBackups(folder: DocumentFile) {
        val backups = folder.listFiles()
            .filter { it.name?.startsWith(FILE_PREFIX) == true }
            .sortedByDescending { it.lastModified() }

        if (backups.size > RETENTION_COUNT) {
            backups.drop(RETENTION_COUNT).forEach { it.delete() }
        }
    }
}