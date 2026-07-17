package com.example.taxflow.di

import com.example.taxflow.billigmanager.BillingManager
import com.example.taxflow.data.PdfReportGenerator
import com.example.taxflow.data.local.database.AppDatabase
import com.example.taxflow.data.notification.NotificationHelper
import com.example.taxflow.data.notification.NotifiedRemindersDataStore
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.data.orc.ReceiptTextRecognizer
import com.example.taxflow.data.repository.CategoryRepository
import com.example.taxflow.data.repository.CategoryRepositoryImpl
import com.example.taxflow.data.repository.PremiumRepository
import com.example.taxflow.data.repository.PremiumRepositoryImpl
import com.example.taxflow.data.repository.SettingsRepository
import com.example.taxflow.data.repository.SettingsRepositoryImpl
import com.example.taxflow.data.repository.TaxDeadlineRepository
import com.example.taxflow.data.repository.TaxDeadlineRepositoryImpl
import com.example.taxflow.data.repository.TransactionRepository
import com.example.taxflow.data.repository.TransactionRepositoryImpl
import com.example.taxflow.data.settings.PremiumStatusDataStore
import com.example.taxflow.data.settings.SettingsDataStore
import com.example.taxflow.domain.usecase.CalculateTaxReserveUseCase
import com.example.taxflow.viewModel.AddTransactionViewModel
import com.example.taxflow.viewModel.DashboardViewModel
import com.example.taxflow.viewModel.DeadlinesViewModel
import com.example.taxflow.viewModel.ExportViewModel
import com.example.taxflow.viewModel.OverviewViewModel
import com.example.taxflow.viewModel.PaywallViewModel
import com.example.taxflow.viewModel.PremiumStatusViewModel
import com.example.taxflow.viewModel.ReceiptScanViewModel
import com.example.taxflow.viewModel.SettingsViewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel

val appModule = module {

    // Database & DAOs
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().taxDeadlineDao() }

    // Settings (DataStore)
    single { SettingsDataStore(androidContext()) }
    single { BillingManager(androidContext()) }
    single { PremiumStatusDataStore(androidContext()) }
    single { ReceiptTextRecognizer() }
    single { ReceiptDraftHolder() }
    single { PdfReportGenerator(androidContext()) }
    single { NotificationHelper(androidContext()) }
    single { NotifiedRemindersDataStore(androidContext()) }
    // Repositories
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<TaxDeadlineRepository> { TaxDeadlineRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PremiumRepository> { PremiumRepositoryImpl(get(), get()) }

    // Use cases
    factory { CalculateTaxReserveUseCase() }

    // ViewModels
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { AddTransactionViewModel(get(), get(), get()) }
    viewModel { OverviewViewModel(get()) }
    viewModel { DeadlinesViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PaywallViewModel(get(), get()) }
    viewModel { PremiumStatusViewModel(get()) }
    viewModel { ReceiptScanViewModel(get(), get()) }
    viewModel { ExportViewModel(get(), get(), get()) }

}