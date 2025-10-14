package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.model.emptyProfile
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

data class AuthUiState(
    val isInProgress: Boolean = false,
    val errorMessage: String? = null,
    val isSignUp: Boolean = false
)

data class ProfileUiState(
    val profile: Profile? = null,
    val errorMessage: String? = null
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileUiState = currentName
        .filter { it.isNotEmpty() }
        .mapLatest { currentName ->
            try {
                val profile = networkRepository.getProfile(currentName)
                ProfileUiState(profile = profile, errorMessage = null)
            } catch (e: IOException) {
                Timber.d(e)
                ProfileUiState(
                    errorMessage = "No connection with the server"
                )
            } catch (e: SocketTimeoutException) {
                Timber.d(e)
                ProfileUiState(
                    errorMessage = "No connection with the server"
                )
            } catch (e: HttpException) {
                if (e.code() in listOf(401, 404)) {
                    Timber.d(e)
                } else {
                    Timber.e(e)
                }
                ProfileUiState(
                    errorMessage = when (e.code()) {
                        401 -> "Your session has expired, please log in again to continue"
                        404 -> "User not found"
                        500 -> "Error while receiving info, this issue is reported"
                        else -> e.toString()
                    }
                )
            } catch (e: SerializationException) {
                Timber.e(e)
                ProfileUiState(
                    errorMessage = "Error while receiving info, this issue is reported"
                )
            }
        }
        .scan(ProfileUiState()) { previous, result ->
            // If fetch succeeded, result.profile is non-null: use it.
            // Otherwise, keep previous.profile and only update errorMessage.
            if (result.profile != null) {
                result
            } else {
                previous.copy(errorMessage = result.errorMessage)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
            initialValue = ProfileUiState()
        )

    private val profileEditErrorMessage: StateFlow<String?> = MutableStateFlow(null)
    private val notNullProfileUiState = profileUiState.filter { it.profile != null }
    val displayNameTextFieldState = TextFieldState()
    val bioTextFieldState = TextFieldState()

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
                } catch (e: HttpException) {
                    if (e.code() == 401) {
                        Timber.d(e)
                    } else {
                        Timber.e(e)
                    }
                    _authUiState.update {
                        it.copy(
                            errorMessage = when (e.code()) {
                                401 -> "Incorrect username or password"
                                else -> e.toString()
                            },
                            isInProgress = false
                        )
                    }
                } catch (e: UnknownHostException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: SocketTimeoutException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: ConnectException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
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
                } catch (e: HttpException) {
                    if (e.code() == 401) {
                        Timber.d(e)
                    } else {
                        Timber.e(e)
                    }
                    _authUiState.update {
                        it.copy(
                            errorMessage = when (e.code()) {
                                401 -> "Error, check your credentials and try again"
                                else -> e.toString()
                            },
                            isInProgress = false
                        )
                    }
                } catch (e: UnknownHostException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: SocketTimeoutException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: ConnectException) {
                    Timber.d(e)
                    _authUiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
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
