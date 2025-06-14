package dev.stock.dysnomia

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.ui.composables.DysnomiaBottomNavigationBar
import dev.stock.dysnomia.ui.screen.chat.ChatScreen
import dev.stock.dysnomia.ui.screen.chat.ChatViewModel
import dev.stock.dysnomia.ui.screen.chat.ConnectionState
import dev.stock.dysnomia.ui.screen.home.HomeScreen
import dev.stock.dysnomia.ui.screen.home.HomeUiState
import dev.stock.dysnomia.ui.screen.profile.LoadingScreen
import dev.stock.dysnomia.ui.screen.profile.LoginScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileUiState
import dev.stock.dysnomia.ui.screen.profile.ProfileViewModel

enum class DysnomiaApp {
    Home, Chat, Profile
}

@Composable
fun DysnomiaApp(
    chatViewModel: ChatViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
//    homeViewModel: HomeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = DysnomiaApp.valueOf(
        backStackEntry?.destination?.route ?: DysnomiaApp.Home.name
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val currentName = profileViewModel.currentName.collectAsState().value

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DysnomiaBottomNavigationBar(
                currentScreen = currentScreen,
                onClick = {
                    if (it != currentScreen) {
                        navController.navigate(it.name) {
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = DysnomiaApp.Home.name,
        ) {
            composable(route = DysnomiaApp.Home.name) {
                val homeUiState = HomeUiState(currentName)

                HomeScreen(
                    uiState = homeUiState,
                    onChatClicked = {
                        if (it == 0) {
                            navController.navigate(DysnomiaApp.Chat.name) {
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = DysnomiaApp.Chat.name) {
                val chatUiState = chatViewModel.chatUiState.collectAsState().value
                val chatHistory = chatViewModel.chatHistory.collectAsState(emptyList()).value

                LaunchedEffect(chatUiState.connectionState) {
                    if (chatUiState.connectionState == ConnectionState.Connecting) {
                        snackbarHostState.showSnackbar(
                            message = "Loading messages (・_・ヾ",
                            withDismissAction = true
                        )
                    }
                }

                ChatScreen(
                    chatHistory = chatHistory,
                    messageText = chatViewModel.messageText,
                    currentName = currentName,
                    onTextChange = chatViewModel::changeChatText,
                    onSendMessage = chatViewModel::sendMessage,
                    onSendCommand = chatViewModel::sendCommand,
                    modifier = Modifier.padding(contentPadding),
                    isCommandPending = chatUiState.isCommandPending
                )
            }

            composable(route = DysnomiaApp.Profile.name) {
                val profileUiState = profileViewModel.uiState
                val username = profileViewModel.username
                val password = profileViewModel.password

                if (currentName == "") {
                    if (profileUiState is ProfileUiState.Error) {
                        LaunchedEffect(true) {
                            val result = snackbarHostState.showSnackbar(
                                message = profileUiState.errorMessage,
                                withDismissAction = true
                            )
                            if (result == SnackbarResult.Dismissed) {
                                profileViewModel.hideError()
                            }
                        }
                    }
                    if (profileUiState is ProfileUiState.AuthInProgress) {
                        LoadingScreen(modifier = Modifier.padding(contentPadding))
                    } else {
                        LoginScreen(
                            username = username,
                            password = password,
                            onNameChange = profileViewModel::changeName,
                            onPasswordChange = profileViewModel::changePassword,
                            onLoginClick = {
                                profileViewModel.signIn(
                                    SignInBody(
                                        username = username.text,
                                        password = password.text
                                    )
                                )
                            },
                            onRegisterClick = {
                                profileViewModel.signUp(
                                    SignUpBody(
                                        username = username.text,
                                        email = "${username.text.trim()}@femboymatrix.su",
                                        password = password.text
                                    )
                                )
                            },
                            modifier = Modifier.padding(contentPadding)
                        )
                    }
                } else {
                    ProfileScreen(
                        name = currentName,
                        onLogoutClick = profileViewModel::logout,
                        modifier = Modifier.padding(contentPadding)
                    )
                }
            }
        }
    }
}
