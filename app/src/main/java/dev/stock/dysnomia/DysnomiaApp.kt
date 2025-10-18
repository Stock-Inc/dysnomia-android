package dev.stock.dysnomia

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.ui.screen.auth.AuthScreen
import dev.stock.dysnomia.ui.screen.auth.AuthViewModel
import dev.stock.dysnomia.ui.screen.chat.ChatScreen
import dev.stock.dysnomia.ui.screen.chat.ChatViewModel
import dev.stock.dysnomia.ui.screen.home.HomeScreen
import dev.stock.dysnomia.ui.screen.introduction.IntroductionFirstStepScreen
import dev.stock.dysnomia.ui.screen.introduction.IntroductionSecondStepScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileEditScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileViewModel
import kotlinx.serialization.Serializable

@SuppressLint("NewApi")
@Composable
fun DysnomiaApp(
    firstLaunch: Boolean,
    chatViewModel: ChatViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    Scaffold { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = if (firstLaunch) Screen.Introduction::class else Screen.Home::class,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable<Screen.Introduction> {
                val args = it.toRoute<Screen.Introduction>()
                val context = LocalContext.current

                fun finishIntroduction() {
                    profileViewModel.setNotFirstLaunch()
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                val hasNotificationsPermission = remember {
                    mutableStateOf(
                        VERSION.SDK_INT < VERSION_CODES.TIRAMISU ||
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                when (args.step) {
                    1 -> {
                        IntroductionFirstStepScreen(
                            onGetStartedClick = {
                                if (hasNotificationsPermission.value) {
                                    finishIntroduction()
                                } else {
                                    navController.navigate(Screen.Introduction(2))
                                }
                            }
                        )
                    }

                    2 -> {
                        IntroductionSecondStepScreen(
                            onProceed = { finishIntroduction() }
                        )
                    }
                }
            }

            composable<Screen.Home> {
                HomeScreen(
                    onChatClicked = {
                        navController.navigate(Screen.Chat) {
                            launchSingleTop = true
                        }
                    },
                    onProfileClicked = {
                        navController.navigate(Screen.Profile) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<Screen.Chat> {
                val chatUiState = chatViewModel.chatUiState.collectAsStateWithLifecycle().value
                val chatHistory = chatViewModel.chatHistory.collectAsStateWithLifecycle(emptyList()).value
                val currentName = profileViewModel.currentName.collectAsStateWithLifecycle().value
                val commandSuggestions = chatViewModel.commandSuggestions.collectAsStateWithLifecycle().value
                val messageTextFieldState = chatViewModel.messageTextFieldState

                ChatScreen(
                    chatHistory = chatHistory,
                    messageTextFieldState = messageTextFieldState,
                    chatUiState = chatUiState,
                    currentName = currentName,
                    commandSuggestions = commandSuggestions,
                    onSendMessage = chatViewModel::sendMessage,
                    onSendCommand = chatViewModel::sendCommand,
                    onReply = chatViewModel::replyTo,
                    onCancelReply = chatViewModel::cancelReply,
                    onNavigateUp = navController::navigateUp,
                    getRepliedMessageStateFlow = chatViewModel::getMessageStateFlowByMessageId
                )
            }

            composable<Screen.Profile> {
                val currentName = profileViewModel.currentName.collectAsStateWithLifecycle().value

                if (currentName == "") {
                    val authUiState = authViewModel.authUiState.collectAsStateWithLifecycle().value
                    val usernameTextFieldState = authViewModel.usernameTextFieldState
                    val emailTextFieldState = authViewModel.emailTextFieldState
                    val passwordTextFieldState = authViewModel.passwordTextFieldState

                    AuthScreen(
                        authUiState = authUiState,
                        usernameTextFieldState = usernameTextFieldState,
                        emailTextFieldState = emailTextFieldState,
                        passwordTextFieldState = passwordTextFieldState,
                        onProceed = {
                            if (authUiState.isSignUp) {
                                authViewModel.signUp(
                                    SignUpBody(
                                        username = usernameTextFieldState.text.trim().toString(),
                                        email = emailTextFieldState.text.trim().toString(),
                                        password = passwordTextFieldState.text.trim().toString()
                                    )
                                )
                            } else {
                                authViewModel.signIn(
                                    SignInBody(
                                        username = usernameTextFieldState.text.trim().toString(),
                                        password = passwordTextFieldState.text.trim().toString()
                                    )
                                )
                            }
                        },
                        onChangeAuthScreen = authViewModel::changeAuthScreen
                    )
                } else {
                    val profileUiState = profileViewModel.profileUiState.collectAsStateWithLifecycle().value

                    ProfileScreen(
                        profileUiState = profileUiState,
                        onEditProfileClick = { navController.navigate(Screen.ProfileEdit) },
                        onLogoutClick = authViewModel::logout,
                        isUserMe = true,
                        onRefresh = profileViewModel::refreshProfile
                    )
                }
            }

            composable<Screen.ProfileEdit> {
                val profileEditUiState = profileViewModel.profileEditUiState
                    .collectAsStateWithLifecycle().value

                ProfileEditScreen(
                    profileEditUiState = profileEditUiState,
                    onChangeImage = {}, // TODO(DYS-14)
                    onSaveClick = {
                        profileViewModel.changeProfile(it)
                        navController.navigateUp()
                    },
                    onBackPressed = navController::navigateUp
                )
            }
        }
    }
}

@Serializable
sealed class Screen {
    @Serializable
    data class Introduction(val step: Int = 1) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Chat : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object ProfileEdit : Screen()
}
