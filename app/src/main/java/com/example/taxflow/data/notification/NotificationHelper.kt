package com.example.taxflow.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.taxflow.domain.model.TaxDeadline
import java.time.format.DateTimeFormatter

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "deadline_reminders"
        private const val CHANNEL_NAME = "Fristen-Erinnerungen"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Erinnerungen an bevorstehende Steuerfristen"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showDeadlineReminder(deadline: TaxDeadline, daysUntil: Int) {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val title = when (daysUntil) {
            0 -> "Heute fällig: ${deadline.title}"
            1 -> "Morgen fällig: ${deadline.title}"
            else -> "${deadline.title} in $daysUntil Tagen fällig"
        }
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val text = "Fällig am ${deadline.dueDate.format(dateFormatter)}" +
                if (deadline.note.isNotBlank()) " – ${deadline.note}" else ""

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: eigenes App-Icon einsetzen
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(deadline.id.toInt(), notification)
    }
}
