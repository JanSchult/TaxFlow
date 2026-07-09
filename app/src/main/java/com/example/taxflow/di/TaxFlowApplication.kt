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
        startKoin {
            CoroutineScope(Dispatchers.IO).launch {
                get<PremiumRepository>().refreshFromPlayStore()
            }
                androidContext(this@TaxFlowApplication)
                modules(appModule)
        }
    }
}