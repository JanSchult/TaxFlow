package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.backup.AutoBackupDataStore
import com.example.taxflow.data.backup.AutoBackupSettings
import com.example.taxflow.data.backup.BackupManager
import com.example.taxflow.viewModel.uiState.BackupUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class BackupViewModel(
    private val backupManager: BackupManager,
    private val autoBackupDataStore: AutoBackupDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState

    val autoBackupSettings: StateFlow<AutoBackupSettings> = autoBackupDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AutoBackupSettings())
    fun startExport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            try {
                val json = backupManager.exportToJson()
                _uiState.value = _uiState.value.copy(isWorking = false, exportedJson = json)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    isError = true,
                    message = "Export fehlgeschlagen. Bitte erneut versuchen."
                )
            }
        }
    }

    /** Von der UI aufrufen, nachdem der SAF-Dialog behandelt wurde. */
    fun exportHandled() {
        _uiState.value = _uiState.value.copy(exportedJson = null)
    }

    fun restore(json: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            try {
                backupManager.restoreFromJson(json)
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    isError = false,
                    message = "Wiederherstellung abgeschlossen."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    isError = true,
                    message = "Wiederherstellung fehlgeschlagen. Ist die Datei ein gültiges TaxFlow-Backup?"
                )
            }
        }
    }
    fun onAutoBackupFolderSelected(uriString: String) {
        viewModelScope.launch {
            autoBackupDataStore.setFolderUri(uriString)
        }
    }

    fun onAutoBackupToggled(enabled: Boolean) {
        viewModelScope.launch {
            autoBackupDataStore.setEnabled(enabled)
        }
    }
}

