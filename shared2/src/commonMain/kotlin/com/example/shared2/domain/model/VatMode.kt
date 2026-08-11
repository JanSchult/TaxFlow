package com.example.shared2.domain.model

enum class VatMode(val label: String, val ratePercent: Double?) {
    NONE("Kleinunternehmer §19 (keine USt)", null),
    STANDARD("Regelbesteuerung 19%", 19.0),
    REDUCED("Regelbesteuerung 7%", 7.0)
}