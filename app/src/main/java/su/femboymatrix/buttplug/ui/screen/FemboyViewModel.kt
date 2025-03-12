package su.femboymatrix.buttplug.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import su.femboymatrix.buttplug.data.ChatHistoryEntity
import su.femboymatrix.buttplug.data.FemboyNetworkRepository
import su.femboymatrix.buttplug.data.FemboyOfflineRepository
import su.femboymatrix.buttplug.utils.TIMEOUT_MILLIS
import javax.inject.Inject

data class ConsoleUiState(
    val text: String = ""
)

@HiltViewModel
class FemboyViewModel @Inject constructor(
    private val femboyNetworkRepository: FemboyNetworkRepository,
    private val femboyOfflineRepository: FemboyOfflineRepository
) : ViewModel() {
    private val _consoleUiState = MutableStateFlow(ConsoleUiState())
    val consoleUiState = _consoleUiState.asStateFlow()

    val consoleHistory: Flow<List<ChatHistoryEntity>> =
        femboyOfflineRepository.getAllHistory().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    fun sendCommand() {
        if (_consoleUiState.value.text.isNotEmpty()) {
            viewModelScope.launch {
                femboyOfflineRepository.addToHistory(
                    ChatHistoryEntity(
                        command = _consoleUiState.value.text.trim(),
                        result = femboyNetworkRepository.sendMessage(
                            _consoleUiState.value.text.trim()
                        )
                    )
                )
            }
            _consoleUiState.update {
                it.copy(
                    text = ""
                )
            }
        }
    }

    fun changeConsoleText(text: String) {
        _consoleUiState.update {
            it.copy(
                text = text
            )
        }
    }
}
