package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

data class AuthUiState(
    val isInProgress: Boolean = false,
    val errorMessage: String? = null,
    val isSignUp: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: PreferencesRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    var username by mutableStateOf(TextFieldValue())
        private set
    var password by mutableStateOf(TextFieldValue())
        private set
    var email by mutableStateOf(TextFieldValue())
        private set

    val currentName = userPreferencesRepository.name.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS),
        initialValue = ""
    )

    fun changeName(username: TextFieldValue) {
        this.username = username
    }

    fun changePassword(password: TextFieldValue) {
        this.password = password
    }

    fun changeEmail(email: TextFieldValue) {
        this.email = email
    }

    fun signIn(signInBody: SignInBody) {
        if (signInBody.username.isNotEmpty() && signInBody.password.isNotEmpty()) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isInProgress = true
                    )
                }

                try {
                    val signInResult = networkRepository.signIn(signInBody)

                    userPreferencesRepository.saveAccount(
                        name = signInBody.username,
                        accessToken = signInResult.accessToken,
                        refreshToken = signInResult.refreshToken
                    )

                    _uiState.value = AuthUiState()
                    password = TextFieldValue()
                } catch (e: HttpException) {
                    Timber.d(e)
                    _uiState.update {
                        it.copy(
                            errorMessage = when (e.code()) {
                                401 -> "Incorrect username or password"
                                500 -> "Error, check your credentials and try again"
                                else -> e.toString()
                            },
                            isInProgress = false
                        )
                    }
                } catch (e: UnknownHostException) {
                    Timber.d(e)
                    _uiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: ConnectException) {
                    Timber.d(e)
                    _uiState.update {
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
                _uiState.update {
                    it.copy(
                        isInProgress = true
                    )
                }

                try {
                    val signUpResult = networkRepository.signUp(signUpBody)

                    userPreferencesRepository.saveAccount(
                        name = signUpBody.username,
                        accessToken = signUpResult.accessToken,
                        refreshToken = signUpResult.refreshToken
                    )

                    _uiState.value = AuthUiState()
                    password = TextFieldValue()
                } catch (e: HttpException) {
                    Timber.d(e)
                    _uiState.update {
                        it.copy(
                            errorMessage = when (e.code()) {
                                409 -> "User already exists"
                                500 -> "Error, check your credentials and try again"
                                else -> e.toString()
                            },
                            isInProgress = false
                        )
                    }
                } catch (e: UnknownHostException) {
                    Timber.d(e)
                    _uiState.update {
                        it.copy(
                            errorMessage = "No connection with the server",
                            isInProgress = false
                        )
                    }
                } catch (e: ConnectException) {
                    Timber.d(e)
                    _uiState.update {
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
            _uiState.value = AuthUiState()
        }
    }

    fun changeAuthScreen(isSignUp: Boolean) {
        _uiState.value = AuthUiState(isSignUp = isSignUp)
    }
}
