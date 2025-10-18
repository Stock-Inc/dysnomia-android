package dev.stock.dysnomia.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.ChangeProfileBody
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.model.emptyProfile
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

data class ProfileUiState(
    val profile: Profile? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

data class ProfileEditUiState(
    val profile: Profile,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    val currentName = preferencesRepository.name.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
        initialValue = ""
    )

    private val refreshTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileUiState = refreshTrigger
        .onStart { emit(Unit) }
        .onEach { isRefreshing.value = true }
        .flatMapLatest {
            currentName
                .filterNot { it.isEmpty() }
                .distinctUntilChanged()
                .map { name -> fetchProfile(name) }
                .onEach { isRefreshing.value = false }
        }
        .combine(isRefreshing) { state, refreshing ->
            state.copy(isRefreshing = refreshing)
        }
        .scan(ProfileUiState()) { previous, result ->
            when {
                result.profile != null -> result
                else -> previous.copy(
                    errorMessage = result.errorMessage,
                    isRefreshing = result.isRefreshing
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
            initialValue = ProfileUiState()
        )

    fun refreshProfile() {
        refreshTrigger.tryEmit(Unit)
    }

    private suspend fun fetchProfile(name: String): ProfileUiState {
        return try {
            val profile = networkRepository.getProfile(name)
            ProfileUiState(profile = profile, errorMessage = null)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            when (e) {
                is IOException -> {
                    Timber.d(e)
                    ProfileUiState(errorMessage = "No connection with the server")
                }

                is HttpException -> {
                    ProfileUiState(errorMessage = handleBasicHttpException(e))
                }

                else -> {
                    Timber.e(e)
                    ProfileUiState(errorMessage = "An unexpected error occurred")
                }
            }
        }
    }

    private val profileEditErrorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val notNullProfileUiState = profileUiState.filter { it.profile != null }

    val profileEditUiState =
        combine(
            notNullProfileUiState,
            profileEditErrorMessage
        ) { profileUiState, errorMessage ->
            ProfileEditUiState(
                profile = profileUiState.profile!!,
                errorMessage = errorMessage
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
            initialValue = ProfileEditUiState(emptyProfile)
        )

    fun changeProfile(changeProfileBody: ChangeProfileBody) {
        viewModelScope.launch {
            try {
                networkRepository.changeProfile(changeProfileBody)
                refreshProfile()
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                when (e) {
                    is IOException -> {
                        Timber.d(e)
                        profileEditErrorMessage.value = "No connection with the server"
                    }

                    is HttpException -> {
                        profileEditErrorMessage.value = handleBasicHttpException(e)
                    }

                    else -> {
                        Timber.e(e)
                        profileEditErrorMessage.value = "An unexpected error occurred"
                    }
                }
            }
        }
    }

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            preferencesRepository.setNotFirstLaunch()
        }
    }

    private fun handleBasicHttpException(e: HttpException): String {
        if (e.code() in listOf(401, 404)) {
            Timber.d(e)
        } else {
            Timber.e(e)
        }
        return when (e.code()) {
            401 -> "Your session has expired, please log in again to continue"
            404 -> "Your requested info is not found"
            500 -> "Error while receiving info, this issue is reported"
            else -> "Server error: ${e.code()}"
        }
    }
}
