package dev.stock.dysnomia.ui.screen.auth

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

data class AuthUiState(
    val isInProgress: Boolean = false,
    val errorMessage: String? = null,
    val isSignUp: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
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

                    preferencesRepository.saveAccount(
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
                            _authUiState.update {
                                it.copy(
                                    errorMessage = "An unexpected error occurred",
                                    isInProgress = false
                                )
                            }
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

                    preferencesRepository.saveAccount(
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
                            _authUiState.update {
                                it.copy(
                                    errorMessage = "An unexpected error occurred",
                                    isInProgress = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.clearAccount()
            _authUiState.value = AuthUiState()
        }
    }

    fun changeAuthScreen(isSignUp: Boolean) {
        _authUiState.value = AuthUiState(isSignUp = isSignUp)
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
}
