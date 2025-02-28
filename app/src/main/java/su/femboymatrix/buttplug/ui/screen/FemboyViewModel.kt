package su.femboymatrix.buttplug.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import su.femboymatrix.buttplug.data.FemboyNetworkRepository
import javax.inject.Inject

data class ConsoleUiState(
    val text: String = "",
    val commandHistory: List<Map<String, Any>> = emptyList()
)

@HiltViewModel
class FemboyViewModel @Inject constructor(
    private val femboyNetworkRepository: FemboyNetworkRepository
) : ViewModel() {
    private val _consoleUiState = MutableStateFlow(ConsoleUiState())
    val consoleUiState = _consoleUiState.asStateFlow()

    fun sendCommand() {
        if (_consoleUiState.value.text.isNotEmpty()) {
            viewModelScope.launch {
                _consoleUiState.update {
                    it.copy(
                        commandHistory = it.commandHistory +
                                mapOf(
                                    "id" to it.commandHistory.size,
                                    "command" to it.text.trim(),
                                    "result" to femboyNetworkRepository.sendCommand(
                                        it.text.trim()
                                    )
                                ),
                        text = ""
                    )
                }
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