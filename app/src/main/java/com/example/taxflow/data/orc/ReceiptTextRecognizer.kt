package com.example.taxflow.data.orc

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Kapselt ML Kit Text Recognition v2 (Bundled Latin-Modell, com.google.mlkit:text-recognition:16.0.1).
 * Läuft komplett on-device: kein Netzwerk, keine Cloud-Kosten, kein Foto verlässt das Gerät.
 */
class ReceiptTextRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognize(bitmap: Bitmap): String = suspendCancellableCoroutine { cont ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText -> cont.resume(visionText.text) }
            .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }
}