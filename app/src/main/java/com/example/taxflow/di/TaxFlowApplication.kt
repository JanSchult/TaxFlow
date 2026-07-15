package com.example.taxflow.di

import android.app.Application
import com.example.taxflow.data.repository.PremiumRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.android.ext.android.get

class TaxFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. ERST Koin komplett starten und die Module registrieren ...
        startKoin {
            androidContext(this@TaxFlowApplication)
            modules(appModule)
        }

        // 2. ... und ERST DANACH, außerhalb der startKoin{}-Lambda, etwas auflösen.
        CoroutineScope(Dispatchers.IO).launch {
            get<PremiumRepository>().refreshFromPlayStore()
        }
    }
}