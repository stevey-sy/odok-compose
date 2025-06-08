package com.sy.odokcompose.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.database.export.DatabaseExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val databaseExporter: DatabaseExporter
) : ViewModel() {
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    fun exportDatabase() {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            databaseExporter.exportBooks()
                .onSuccess { file ->
                    _exportState.value = ExportState.Success(file)
                }
                .onFailure { error ->
                    _exportState.value = ExportState.Error(error.message ?: "알 수 없는 오류가 발생했습니다")
                }
        }
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    data class Success(val file: java.io.File) : ExportState()
    data class Error(val message: String) : ExportState()
} 