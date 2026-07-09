package com.example.taxflow.data.orc

import com.example.taxflow.domain.model.ReceiptScanData
import java.time.LocalDate
import java.util.Locale

/**
 * Regelbasierte (nicht KI-gestützte) Interpretation des von ML Kit erkannten Rohtexts.
 * Bewusst simpel gehalten: Betrag, Datum und Händlername werden nur GESCHÄTZT.
 * Der Nutzer bekommt das Ergebnis im ReceiptScanScreen immer zur Korrektur vorgelegt –
 * es wird nie automatisch als Buchung übernommen.
 */
object ReceiptParser {

    private val totalKeywords = listOf(
        "gesamt", "summe", "total", "zu zahlen", "betrag", "endbetrag", "rechnungsbetrag"
    )

    // Deutsches Zahlenformat: 1.234,56 oder 12,50 - optional mit €/EUR dahinter
    private val amountRegex = Regex("""(\d{1,3}(?:[.,]\d{3})*[.,]\d{2})\s*(?:€|eur)?""", RegexOption.IGNORE_CASE)
    private val dateRegex = Regex("""(\d{1,2})[.](\d{1,2})[.](\d{2,4})""")

    fun parse(rawText: String): ReceiptScanData {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        return ReceiptScanData(
            amount = extractAmount(lines),
            date = extractDate(rawText),
            vendorGuess = lines.firstOrNull(),
            rawText = rawText
        )
    }

    private fun extractAmount(lines: List<String>): Double? {
        // 1. Bevorzugt: Zeile mit "Gesamt"/"Summe"/"Total" etc.
        for (line in lines) {
            val lower = line.lowercase(Locale.GERMANY)
            if (totalKeywords.any { lower.contains(it) }) {
                amountRegex.find(line)?.let { match ->
                    parseGermanAmount(match.groupValues[1])?.let { return it }
                }
            }
        }
        // 2. Fallback: größter im gesamten Text gefundener Betrag
        // (auf Kassenbons ist die Endsumme meist der größte Wert)
        return lines
            .flatMap { line -> amountRegex.findAll(line).map { it.groupValues[1] } }
            .mapNotNull { parseGermanAmount(it) }
            .maxOrNull()
    }

    private fun extractDate(rawText: String): LocalDate? {
        val match = dateRegex.find(rawText) ?: return null
        return try {
            val day = match.groupValues[1].toInt()
            val month = match.groupValues[2].toInt()
            var year = match.groupValues[3].toInt()
            if (year < 100) year += 2000
            LocalDate.of(year, month, day)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseGermanAmount(raw: String): Double? {
        val normalized = raw.replace(".", "").replace(",", ".")
        return normalized.toDoubleOrNull()
    }
}