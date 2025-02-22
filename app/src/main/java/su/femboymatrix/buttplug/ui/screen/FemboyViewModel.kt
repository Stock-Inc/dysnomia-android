package su.femboymatrix.buttplug.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import su.femboymatrix.buttplug.FemboyApplication
import su.femboymatrix.buttplug.data.FemboyNetworkRepository

data class ConsoleUiState(
    val text: String = "",
    val commandHistory: List<String> = emptyList()
)

class FemboyViewModel(
    private val femboyNetworkRepository: FemboyNetworkRepository
) : ViewModel() {
    private val _consoleUiState = MutableStateFlow(ConsoleUiState())
    val consoleUiState = _consoleUiState.asStateFlow()

    fun sendCommand() {
        if (_consoleUiState.value.text.isNotEmpty()) {
            viewModelScope.launch {
                _consoleUiState.update {
                    it.copy(
                        commandHistory = it.commandHistory + femboyNetworkRepository.sendCommand(_consoleUiState.value.text),
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FemboyApplication)
                FemboyViewModel(application.container.femboyNetworkRepository)
            }
        }
    }
}