package com.example.taxflow.di

import android.app.Application
import com.example.shared2.di.sharedModule
import com.example.shared2.di.sharedPlatformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class TaxFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TaxFlowApplication)
            modules(
                sharedModule,         // KMP Common Core
                sharedPlatformModule, // KMP Android-Spezifisches (DB, Billing, DataStore)
                appModule             // Android ViewModels & App-UI-Services
            )
        }
    }
}