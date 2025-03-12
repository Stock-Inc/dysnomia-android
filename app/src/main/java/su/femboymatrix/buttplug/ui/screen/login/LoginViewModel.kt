package su.femboymatrix.buttplug.ui.screen.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val name: String = "",
    val password: String = ""
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun changeName(name: String) {
        _uiState.update {
            it.copy(
                name = name
            )
        }
    }

    fun changePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password
            )
        }
    }
}
