package com.example.taxflow.di

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taxflow.data.notification.DeadlineReminderWorker
import com.example.taxflow.data.repository.PremiumRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit

class TaxFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. ERST Koin komplett starten und die Module registrieren ...
        startKoin {
            androidContext(this@TaxFlowApplication)
            modules(appModule)
        }

        // 2. ... und ERST DANACH auflösen bzw. planen.
        CoroutineScope(Dispatchers.IO).launch {
            get<PremiumRepository>().refreshFromPlayStore()
        }
        scheduleDeadlineReminders()
    }

    private fun scheduleDeadlineReminders() {
        val request = PeriodicWorkRequestBuilder<DeadlineReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DeadlineReminderWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}