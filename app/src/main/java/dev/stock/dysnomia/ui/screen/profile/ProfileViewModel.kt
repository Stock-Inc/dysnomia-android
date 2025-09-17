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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface ProfileUiState {
    data object AuthRequired : ProfileUiState
    data object AuthInProgress : ProfileUiState
    data class Error(val errorMessage: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: PreferencesRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    var uiState: ProfileUiState by mutableStateOf(ProfileUiState.AuthRequired)
    var username by mutableStateOf(TextFieldValue())
        private set
    var password by mutableStateOf(TextFieldValue())
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

    fun signIn(signInBody: SignInBody) {
        if (signInBody.username.trim().isNotEmpty() && signInBody.password.trim().isNotEmpty()) {
            viewModelScope.launch {
                uiState = ProfileUiState.AuthInProgress
                try {
                    val signInResult = networkRepository.signIn(
                        SignInBody(
                            username = signInBody.username.trim(),
                            password = signInBody.password.trim()
                        )
                    )

                    userPreferencesRepository.saveAccount(
                        name = signInBody.username.trim(),
                        accessToken = signInResult.accessToken,
                        refreshToken = signInResult.refreshToken
                    )

                    password = TextFieldValue()
                } catch (e: HttpException) {
                    uiState = ProfileUiState.Error(
                        when (e.code()) {
                            401 -> "Incorrect username or password"
                            500 -> "Error, check your credentials and try again"
                            else -> e.toString()
                        }
                    )
                } catch (e: IOException) {
                    uiState = ProfileUiState.Error(
                        if (e.toString().startsWith("java.net.UnknownHostException")) {
                            "No connection with the server (╥﹏╥)"
                        } else {
                            e.toString()
                        }
                    )
                }
            }
        }
    }

    fun signUp(signUpBody: SignUpBody) {
        if (signUpBody.username.trim().isNotEmpty() && signUpBody.password.trim().isNotEmpty()) {
            viewModelScope.launch {
                try {
                    uiState = ProfileUiState.AuthInProgress
                    networkRepository.signUp(
                        SignUpBody(
                            username = signUpBody.username.trim(),
                            password = signUpBody.password.trim()
                        )
                    )

                    signIn(
                        SignInBody(
                            username = signUpBody.username.trim(),
                            password = signUpBody.password.trim()
                        )
                    )
                } catch (e: HttpException) {
                    uiState = ProfileUiState.Error(
                        when (e.code()) {
                            401 -> "Incorrect username or password"
                            500 -> "Error, check your credentials and try again"
                            else -> e.toString()
                        }
                    )
                } catch (e: IOException) {
                    uiState = ProfileUiState.Error(
                        if (e.toString().startsWith("java.net.UnknownHostException")) {
                            "No connection with the server (╥﹏╥)"
                        } else {
                            e.toString()
                        }
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAccount()
            uiState = ProfileUiState.AuthRequired
        }
    }

    fun hideError() {
        uiState = ProfileUiState.AuthRequired
    }
}
