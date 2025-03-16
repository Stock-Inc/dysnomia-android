package su.femboymatrix.buttplug.ui.screen.profile

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import su.femboymatrix.buttplug.data.PreferencesRepository
import su.femboymatrix.buttplug.utils.TIMEOUT_MILLIS
import javax.inject.Inject

data class LoginUiState(
    val name: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    val currentName = userPreferencesRepository.name.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ""
    )

    fun changeName(name: TextFieldValue) {
        _uiState.update {
            it.copy(
                name = name
            )
        }
    }

    fun changePassword(password: TextFieldValue) {
        _uiState.update {
            it.copy(
                password = password
            )
        }
    }

    fun login() {
        val name = _uiState.value.name
        if (name.text.trim() != "") {
            viewModelScope.launch {
                userPreferencesRepository.saveName(name.text.trim())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearName()
        }
    }
}
