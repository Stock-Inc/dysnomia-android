package dev.stock.dysnomia.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
}
