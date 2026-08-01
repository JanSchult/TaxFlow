package com.example.taxflow.data.notification
import com.example.shared2.data.TaxDeadlineRepository
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.daysUntil

class DeadlineReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val deadlineRepository: TaxDeadlineRepository by inject()
    private val notifiedReminders: NotifiedRemindersDataStore by inject()
    private val notificationHelper: NotificationHelper by inject()

    companion object {
        val REMINDER_MILESTONES = listOf(7, 1, 0)
        const val UNIQUE_WORK_NAME = "deadline_reminder_check"
    }

    override suspend fun doWork(): Result {
        return try {
            val openDeadlines = deadlineRepository.getAll().first().filter { !it.isPaid }

            // 1. Aktuelles Datum mit kotlinx-datetime holen
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val alreadyNotified = notifiedReminders.getNotifiedKeys()

            for (deadline in openDeadlines) {
                // 2. Differenz in Tagen berechnen (replacement für ChronoUnit.DAYS.between)
                val daysUntil = today.daysUntil(deadline.dueDate)

                if (daysUntil in REMINDER_MILESTONES) {
                    val reminderKey = "${deadline.id}_$daysUntil"
                    if (reminderKey !in alreadyNotified) {
                        notificationHelper.showDeadlineReminder(deadline, daysUntil)
                        notifiedReminders.markNotified(reminderKey)
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
