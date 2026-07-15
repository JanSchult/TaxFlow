package com.example.taxflow.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.taxflow.domain.model.Category
import com.example.taxflow.domain.model.Transaction
import com.example.taxflow.domain.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Erzeugt PDF-Exporte für den Steuerberater komplett mit Android-Bordmitteln
 * (android.graphics.pdf.PdfDocument) – keine externe Bibliothek, kein
 * zusätzliches Versionsrisiko.
 *
 * Wichtig: kein amtliches/steuerrechtlich geprüftes Dokument, nur eine
 * strukturierte Übersicht zur Vorlage/Weiterverarbeitung durch den Steuerberater.
 */
class PdfReportGenerator(private val context: Context) {

    private val pageWidth = 595  // A4 bei 72dpi
    private val pageHeight = 842
    private val marginLeft = 40f
    private val marginRight = 40f
    private val marginTop = 50f
    private val lineHeight = 20f

    private val titlePaint = Paint().apply { textSize = 18f; typeface = Typeface.DEFAULT_BOLD; color = Color.BLACK }
    private val headerPaint = Paint().apply { textSize = 11f; typeface = Typeface.DEFAULT_BOLD; color = Color.BLACK }
    private val bodyPaint = Paint().apply { textSize = 11f; color = Color.BLACK }
    private val mutedPaint = Paint().apply { textSize = 9f; color = Color.GRAY }

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)

    /** Sammelbericht: alle Transaktionen eines Zeitraums in EINEM PDF, itemisiert + Summen. */
    fun generateSummaryReport(
        transactions: List<Transaction>,
        categories: List<Category>,
        periodLabel: String
    ): File {
        val categoryNames = categories.associateBy({ it.id }, { it.name })
        val document = PdfDocument()

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = drawHeader(canvas, "Einnahmen-Ausgaben-Übersicht", periodLabel, marginTop)
        y += lineHeight
        y = drawTableHeader(canvas, y)

        for (tx in transactions.sortedBy { it.date }) {
            if (y > pageHeight - 80) {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = drawTableHeader(canvas, marginTop)
            }
            y = drawTransactionRow(canvas, tx, categoryNames[tx.categoryId] ?: "Sonstige", y)
        }

        y += lineHeight
        if (y > pageHeight - 140) {
            document.finishPage(page)
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = marginTop
        }
        drawSummaryBlock(canvas, transactions, y)
        document.finishPage(page)

        val outputFile = File(exportsDir(), "TaxFlow_Bericht_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use { document.writeTo(it) }
        document.close()
        return outputFile
    }

    /** Einzelexport: EIN PDF pro Transaktion. */
    fun generateIndividualReceipts(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<File> {
        val categoryNames = categories.associateBy({ it.id }, { it.name })

        return transactions.map { tx ->
            val document = PdfDocument()
            val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
            val canvas = page.canvas
            var y = drawHeader(canvas, "Buchungsbeleg", tx.date.format(dateFormatter), marginTop)
            y += lineHeight * 2

            val typeLabel = if (tx.type == TransactionType.INCOME) "Einnahme" else "Ausgabe"
            val rows = listOf(
                "Datum" to tx.date.format(dateFormatter),
                "Typ" to typeLabel,
                "Kategorie" to (categoryNames[tx.categoryId] ?: "Sonstige"),
                "Betrag" to currencyFormat.format(tx.amount),
                "Notiz" to tx.note.ifBlank { "–" }
            )
            for ((label, value) in rows) {
                canvas.drawText(label, marginLeft, y, headerPaint)
                canvas.drawText(value, marginLeft + 120f, y, bodyPaint)
                y += lineHeight
            }

            document.finishPage(page)
            val outputFile = File(exportsDir(), "Beleg_${tx.date}_${tx.id}.pdf")
            FileOutputStream(outputFile).use { document.writeTo(it) }
            document.close()
            outputFile
        }
    }

    private fun exportsDir(): File = File(context.cacheDir, "exports").apply { mkdirs() }

    private fun drawHeader(canvas: Canvas, title: String, subtitle: String, startY: Float): Float {
        var y = startY
        canvas.drawText("TaxFlow", marginLeft, y, mutedPaint); y += lineHeight
        canvas.drawText(title, marginLeft, y, titlePaint); y += lineHeight
        canvas.drawText(subtitle, marginLeft, y, bodyPaint); y += lineHeight
        canvas.drawText(
            "Erstellt am ${LocalDate.now().format(dateFormatter)} – kein amtliches Dokument, nur zur Vorlage beim Steuerberater",
            marginLeft, y, mutedPaint
        )
        return y + lineHeight
    }

    private fun drawTableHeader(canvas: Canvas, startY: Float): Float {
        canvas.drawText("Datum", marginLeft, startY, headerPaint)
        canvas.drawText("Kategorie", marginLeft + 70f, startY, headerPaint)
        canvas.drawText("Notiz", marginLeft + 220f, startY, headerPaint)
        canvas.drawText("Typ", marginLeft + 380f, startY, headerPaint)
        canvas.drawText("Betrag", pageWidth - marginRight - 60f, startY, headerPaint)
        canvas.drawLine(marginLeft, startY + 4f, pageWidth - marginRight, startY + 4f, mutedPaint)
        return startY + lineHeight
    }

    private fun drawTransactionRow(canvas: Canvas, tx: Transaction, categoryName: String, startY: Float): Float {
        val typeLabel = if (tx.type == TransactionType.INCOME) "Einnahme" else "Ausgabe"
        canvas.drawText(tx.date.format(dateFormatter), marginLeft, startY, bodyPaint)
        canvas.drawText(categoryName.take(22), marginLeft + 70f, startY, bodyPaint)
        canvas.drawText(tx.note.take(25), marginLeft + 220f, startY, bodyPaint)
        canvas.drawText(typeLabel, marginLeft + 380f, startY, bodyPaint)
        canvas.drawText(currencyFormat.format(tx.amount), pageWidth - marginRight - 60f, startY, bodyPaint)
        return startY + lineHeight
    }

    private fun drawSummaryBlock(canvas: Canvas, transactions: List<Transaction>, startY: Float) {
        var y = startY
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val profit = income - expenses

        canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, mutedPaint); y += lineHeight
        canvas.drawText("Gesamteinnahmen", marginLeft, y, headerPaint)
        canvas.drawText(currencyFormat.format(income), pageWidth - marginRight - 80f, y, bodyPaint); y += lineHeight
        canvas.drawText("Gesamtausgaben", marginLeft, y, headerPaint)
        canvas.drawText(currencyFormat.format(expenses), pageWidth - marginRight - 80f, y, bodyPaint); y += lineHeight
        canvas.drawText("Gewinn / Verlust", marginLeft, y, headerPaint)
        canvas.drawText(currencyFormat.format(profit), pageWidth - marginRight - 80f, y, headerPaint)
    }
}
