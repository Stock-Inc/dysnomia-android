package su.femboymatrix.buttplug.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import su.femboymatrix.buttplug.FemboyApplication
import su.femboymatrix.buttplug.data.FemboyNetworkRepository

class FemboyViewModel(
    private val femboyNetworkRepository: FemboyNetworkRepository
) : ViewModel() {
    fun sendCommand(command: String) {
        viewModelScope.launch {
            Log.d("result", femboyNetworkRepository.sendCommand(command))
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