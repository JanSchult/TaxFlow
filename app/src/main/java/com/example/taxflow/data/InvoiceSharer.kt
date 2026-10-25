package com.example.taxflow.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object InvoiceSharer {

    fun sharePdf(context: Context, pdfFile: File, customerEmail: String = "") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(customerEmail))
            putExtra(Intent.EXTRA_SUBJECT, "Rechnung ${pdfFile.nameWithoutExtension}")
            putExtra(
                Intent.EXTRA_TEXT,
                "Guten Tag,\n\nanbei erhalten Sie Ihre Rechnung als PDF.\n\nMit freundlichen Grüßen"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Rechnung senden über...")
        context.startActivity(chooser)
    }
}