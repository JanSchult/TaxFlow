package com.example.taxflow.data.orc

import com.example.taxflow.domain.usecase.ReceiptScanData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReceiptDraftHolder {
    private val _pendingDraft = MutableStateFlow<ReceiptScanData?>(null)
    val pendingDraft: StateFlow<ReceiptScanData?> = _pendingDraft.asStateFlow()

    fun publish(data: ReceiptScanData) {
        _pendingDraft.value = data
    }

    /** Liest den Wert EINMAL aus und leert ihn danach, damit er nicht doppelt übernommen wird. */
    fun consume(): ReceiptScanData? {
        val value = _pendingDraft.value
        _pendingDraft.value = null
        return value
    }
}