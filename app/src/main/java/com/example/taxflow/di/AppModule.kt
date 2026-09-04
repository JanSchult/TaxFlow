package com.example.taxflow.di

import com.example.shared2.data.repository.SettingsRepository
import com.example.shared2.data.local.dao.TaxDeadlineDao
import com.example.shared2.data.local.dao.CategoryDao
import com.example.shared2.data.local.dao.TransactionDao
import com.example.shared2.data.local.database.AppDatabase
import com.example.shared2.data.repository.SettingsRepositoryImpl
import com.example.taxflow.data.OnboardingDataStore
import com.example.taxflow.data.PdfReportGenerator
import com.example.taxflow.data.backup.AutoBackupDataStore
import com.example.taxflow.data.backup.BackupManager
import com.example.taxflow.data.notification.NotificationHelper
import com.example.taxflow.data.notification.NotifiedRemindersDataStore
import com.example.taxflow.data.orc.ReceiptDraftHolder
import com.example.taxflow.data.orc.ReceiptTextRecognizer
import com.example.shared2.settings.SettingsDataStore
import com.example.shared2.usecase.BuildEuerReportUseCase
import com.example.shared2.usecase.CalculateTaxReserveUseCase
import com.example.taxflow.viewModel.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Android UI-Helfer & Manager
    single { SettingsDataStore(get(named("settingsDataStore"))) }
    single { ReceiptTextRecognizer() }
    single { ReceiptDraftHolder() }
    single { PdfReportGenerator(androidContext()) }
    single { NotificationHelper(androidContext()) }
    single { NotifiedRemindersDataStore(androidContext()) }
    single { OnboardingDataStore(androidContext()) }
    single { AutoBackupDataStore(androidContext()) }

    // Backup Manager (Koin löst AppDatabase, DAOs & SettingsRepository automatisch auf)
    single {
        BackupManager(
            database = get(),
            transactionDao = get(),
            categoryDao = get(),
            deadlineDao = get(),
            settingsRepository = get()
        )
    }

    // Repositories & UseCases
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single { CalculateTaxReserveUseCase() } // Registriert für DashboardViewModel
    single{ BuildEuerReportUseCase() }
    // ViewModels
    viewModel { DashboardViewModel(get(), get(), get(), get()) }
    viewModel { AddTransactionViewModel(get(), get(), get(), get()) }
    viewModel { OverviewViewModel(get()) }
    viewModel { DeadlinesViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PaywallViewModel(get(), get()) }
    viewModel { PremiumStatusViewModel(get()) }
    viewModel { ReceiptScanViewModel(get(), get()) }
    viewModel { ExportViewModel(get(), get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { BackupViewModel(get(), get()) }
    viewModel { EuerViewModel(get(), get(), get()) }
}