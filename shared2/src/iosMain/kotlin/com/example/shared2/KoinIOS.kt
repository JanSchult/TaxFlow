package com.example.shared2

import org.koin.core.context.startKoin
import com.example.shared2.di.sharedModule

fun initKoin() {
    startKoin {
        modules(sharedModule) // Füge hier deine bestehenden Koin-Module ein
    }
}