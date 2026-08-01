package com.example.taxflow.domain.usecase

enum class VehicleType(val ratePerKm: Double, val label: String) {
    CAR(0.30, "Pkw"),
    MOTORCYCLE(0.20, "Motorrad/Moped")
}