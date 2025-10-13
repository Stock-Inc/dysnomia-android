package dev.stock.dysnomia

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.ui.screen.chat.ChatScreen
import dev.stock.dysnomia.ui.screen.chat.ChatViewModel
import dev.stock.dysnomia.ui.screen.home.HomeScreen
import dev.stock.dysnomia.ui.screen.introduction.IntroductionFirstStepScreen
import dev.stock.dysnomia.ui.screen.introduction.IntroductionSecondStepScreen
import dev.stock.dysnomia.ui.screen.profile.AuthScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileViewModel
import kotlinx.serialization.Serializable

@SuppressLint("NewApi")
@Composable
fun DysnomiaApp(
    firstLaunch: Boolean,
    chatViewModel: ChatViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
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
                val chatUiState = chatViewModel.chatUiState.collectAsState().value
                val messageTextFieldState = chatViewModel.messageTextFieldState
                val chatHistory = chatViewModel.chatHistory.collectAsState(emptyList()).value
                val currentName = profileViewModel.currentName.collectAsState().value
                val commandSuggestions = chatViewModel.commandSuggestions.collectAsState().value

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
                val currentName = profileViewModel.currentName.collectAsState().value

                if (currentName == "") {
                    val authUiState = profileViewModel.authUiState.collectAsState().value
                    val usernameTextFieldState = profileViewModel.usernameTextFieldState
                    val emailTextFieldState = profileViewModel.emailTextFieldState
                    val passwordTextFieldState = profileViewModel.passwordTextFieldState

                    AuthScreen(
                        authUiState = authUiState,
                        usernameTextFieldState = usernameTextFieldState,
                        emailTextFieldState = emailTextFieldState,
                        passwordTextFieldState = passwordTextFieldState,
                        onProceed = {
                            if (authUiState.isSignUp) {
                                profileViewModel.signUp(
                                    SignUpBody(
                                        username = usernameTextFieldState.text.trim().toString(),
                                        email = emailTextFieldState.text.trim().toString(),
                                        password = passwordTextFieldState.text.trim().toString()
                                    )
                                )
                            } else {
                                profileViewModel.signIn(
                                    SignInBody(
                                        username = usernameTextFieldState.text.trim().toString(),
                                        password = passwordTextFieldState.text.trim().toString()
                                    )
                                )
                            }
                        },
                        onChangeAuthScreen = profileViewModel::changeAuthScreen
                    )
                } else {
                    val profileUiState = profileViewModel.profileUiState.collectAsState().value

                    ProfileScreen(
                        profileUiState = profileUiState,
                        onLogoutClick = profileViewModel::logout,
                        isUserMe = true
                    )
                }
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
}
