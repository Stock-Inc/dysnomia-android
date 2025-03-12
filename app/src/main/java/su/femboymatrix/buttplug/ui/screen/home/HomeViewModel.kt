package su.femboymatrix.buttplug.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import su.femboymatrix.buttplug.data.PreferencesRepository
import su.femboymatrix.buttplug.utils.TIMEOUT_MILLIS
import javax.inject.Inject

data class HomeUiState(
    val name: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: PreferencesRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = userPreferencesRepository.name
        .map {
            HomeUiState(
                name = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
}
