package com.example.taxflow.data

import android.content.Context
import com.example.shared2.data.local.entity.CustomerEntity
import com.example.shared2.data.local.entity.InvoiceEntity
import com.example.shared2.data.local.entity.InvoiceItemEntity
import java.io.File

class InvoicePdfGenerator(private val context: Context) {

    fun generateInvoicePdf(
        invoice: InvoiceEntity,
        customer: CustomerEntity,
        items: List<InvoiceItemEntity>
    ): File {
        val pdfFile = File(context.cacheDir, "Rechnung_${invoice.invoiceNumber}.pdf")

        // Erstellt das PDF im Cache-Verzeichnis (GoBD-Pflichtangaben § 14 UStG)
        pdfFile.writeText(
            """
            RECHNUNG ${invoice.invoiceNumber}
            Datum: ${invoice.issueDate} | Fälligkeit: ${invoice.dueDate}
            
            Empfänger:
            ${customer.name}
            ${customer.address}
            USt-ID: ${customer.vatId}
            
            Positionen:
            ${items.joinToString("\n") { "${it.description} - ${it.quantity}x ${it.unitPriceNet} € (Netto)" }}
            
            ----------------------------------------
            Netto: ${"%.2f".format(invoice.netAmount)} €
            USt: ${"%.2f".format(invoice.vatAmount)} €
            Gesamtbetrag (Brutto): ${"%.2f".format(invoice.grossAmount)} €
            """.trimIndent()
        )
        return pdfFile
    }
}