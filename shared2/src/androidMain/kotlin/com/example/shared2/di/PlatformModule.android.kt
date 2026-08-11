package com.example.shared2.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.shared2.billingmanager.BillingManager
import com.example.shared2.data.local.database.DatabaseBuilderFactory
import com.example.shared2.data.local.database.getRoomDatabase
import com.example.shared2.data.local.database.seedDefaultCategoriesIfNeeded
import com.example.shared2.settings.createAndroidPremiumDataStore
import com.example.shared2.settings.createAndroidSettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedPlatformModule = module {
    // Database
    single {
        val factory = DatabaseBuilderFactory(androidContext())
        val db = getRoomDatabase(factory)
        CoroutineScope(Dispatchers.IO).launch { db.seedDefaultCategoriesIfNeeded() }
        db
    }

    // Billing & DataStores
    single { BillingManager(androidContext()) }
    single<DataStore<Preferences>>(named("premiumDataStore")) {
        createAndroidPremiumDataStore(androidContext())
    }
    single<DataStore<Preferences>>(named("settingsDataStore")) {
        createAndroidSettingsDataStore(androidContext())
    }
}