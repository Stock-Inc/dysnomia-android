package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.ChangeProfileBody
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.model.emptyProfile
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

data class AuthUiState(
    val isInProgress: Boolean = false,
    val errorMessage: String? = null,
    val isSignUp: Boolean = false
)

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
    private val userPreferencesRepository: PreferencesRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
    val authUiState = _authUiState.asStateFlow()

    var usernameTextFieldState = TextFieldState()
        private set
    var passwordTextFieldState = TextFieldState()
        private set
    var emailTextFieldState = TextFieldState()
        private set

    val currentName = userPreferencesRepository.name.stateIn(
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
                .map { name -> fetchProfile(name)}
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

    private fun handleAuthHttpException(e: HttpException): String {
        if (e.code() == 401) {
            Timber.d(e)
        } else {
            Timber.e(e)
        }
        return when (e.code()) {
            401 -> "Incorrect username or password"
            404 -> "Your requested info is not found"
            500 -> "Error while receiving info, this issue is reported"
            else -> "Server error: ${e.code()}"
        }
    }

    private val profileEditErrorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val notNullProfileUiState = profileUiState.filter { it.profile != null }

    val profileEditUiState = combine(
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

    fun signIn(signInBody: SignInBody) {
        if (signInBody.username.isNotEmpty() && signInBody.password.isNotEmpty()) {
            viewModelScope.launch {
                _authUiState.update {
                    it.copy(
                        isInProgress = true
                    )
                }

                try {
                    val signInResult = networkRepository.signIn(signInBody)

                    userPreferencesRepository.saveAccount(
                        name = signInBody.username.toString(),
                        accessToken = signInResult.accessToken,
                        refreshToken = signInResult.refreshToken
                    )

                    _authUiState.value = AuthUiState()
                    passwordTextFieldState.clearText()
                } catch (e: Exception) {
                    coroutineContext.ensureActive()
                    when (e) {
                        is IOException -> {
                            Timber.d(e)
                            _authUiState.update {
                                it.copy(
                                    errorMessage = "No connection with the server",
                                    isInProgress = false
                                )
                            }
                        }
                        is HttpException -> {
                            _authUiState.update {
                                it.copy(
                                    errorMessage = handleAuthHttpException(e),
                                    isInProgress = false
                                )
                            }
                        }
                        else -> {
                            Timber.e(e)
                            profileEditErrorMessage.value = "An unexpected error occurred"
                        }
                    }
                }
            }
        }
    }

    fun signUp(signUpBody: SignUpBody) {
        if (signUpBody.username.isNotEmpty() && signUpBody.password.isNotEmpty()) {
            viewModelScope.launch {
                _authUiState.update {
                    it.copy(
                        isInProgress = true
                    )
                }

                try {
                    val signUpResult = networkRepository.signUp(signUpBody)

                    userPreferencesRepository.saveAccount(
                        name = signUpBody.username.toString(),
                        accessToken = signUpResult.accessToken,
                        refreshToken = signUpResult.refreshToken
                    )

                    _authUiState.value = AuthUiState()
                    passwordTextFieldState.clearText()
                } catch (e: Exception) {
                    coroutineContext.ensureActive()
                    when (e) {
                        is IOException -> {
                            Timber.d(e)
                            _authUiState.update {
                                it.copy(
                                    errorMessage = "No connection with the server",
                                    isInProgress = false
                                )
                            }
                        }
                        is HttpException -> {
                            _authUiState.update {
                                it.copy(
                                    errorMessage = handleAuthHttpException(e),
                                    isInProgress = false
                                )
                            }
                        }
                        else -> {
                            Timber.e(e)
                            profileEditErrorMessage.value = "An unexpected error occurred"
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAccount()
            _authUiState.value = AuthUiState()
        }
    }

    fun changeAuthScreen(isSignUp: Boolean) {
        _authUiState.value = AuthUiState(isSignUp = isSignUp)
    }

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            userPreferencesRepository.setNotFirstLaunch()
        }
    }
}
