package com.example.shared2.di

import com.example.shared2.data.local.database.AppDatabase
import com.example.shared2.data.repository.*
import com.example.shared2.settings.PremiumStatusDataStore
import com.example.shared2.settings.SettingsDataStore
import com.example.shared2.usecase.CalculateTaxReserveUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
    // DAOs (werden aus dem get<AppDatabase>() aufgelöst)
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().taxDeadlineDao() }

    // Shared DataStores & Billing
    single { PremiumStatusDataStore(get()) }
    single { SettingsDataStore(get(named("settingsDataStore"))) }
    single { PremiumStatusDataStore(get(named("premiumDataStore"))) }

    // Repositories
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<TaxDeadlineRepository> { TaxDeadlineRepositoryImpl(get()) }
    single<PremiumRepository> { PremiumRepositoryImpl(get(), get()) }

    // Use Cases
    factory { CalculateTaxReserveUseCase() }
}