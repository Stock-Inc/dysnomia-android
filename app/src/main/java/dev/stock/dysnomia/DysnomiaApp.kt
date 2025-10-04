package dev.stock.dysnomia

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import dev.stock.dysnomia.ui.screen.home.HomeScreen
import dev.stock.dysnomia.ui.screen.home.HomeUiState
import dev.stock.dysnomia.ui.screen.profile.AuthScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileScreen
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

    Scaffold(
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
                val currentName = profileViewModel.currentName.collectAsState().value
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
                val currentName = profileViewModel.currentName.collectAsState().value

                ChatScreen(
                    chatHistory = chatHistory,
                    messageText = chatViewModel.messageText,
                    chatUiState = chatUiState,
                    currentName = currentName,
                    onTextChange = chatViewModel::changeChatText,
                    onSendMessage = chatViewModel::sendMessage,
                    onSendCommand = chatViewModel::sendCommand,
                    onReply = chatViewModel::replyTo,
                    onCancelReply = chatViewModel::cancelReply,
                    getRepliedMessageStateFlow = chatViewModel::getMessageStateFlowByMessageId,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = DysnomiaApp.Profile.name) {
                val currentName = profileViewModel.currentName.collectAsState().value

                if (currentName == "") {
                    val authUiState = profileViewModel.uiState.collectAsState().value
                    val username = profileViewModel.username
                    val email = profileViewModel.email
                    val password = profileViewModel.password

                    AuthScreen(
                        authUiState = authUiState,
                        username = username,
                        email = email,
                        password = password,
                        onNameChange = profileViewModel::changeName,
                        onEmailChange = profileViewModel::changeEmail,
                        onPasswordChange = profileViewModel::changePassword,
                        onProceed = {
                            if (authUiState.isSignUp) {
                                profileViewModel.signUp(
                                    SignUpBody(
                                        username = username.text.trim(),
                                        email = email.text.trim(),
                                        password = password.text.trim()
                                    )
                                )
                            } else {
                                profileViewModel.signIn(
                                    SignInBody(
                                        username = username.text.trim(),
                                        password = password.text.trim()
                                    )
                                )
                            }
                        },
                        onChangeAuthScreen = profileViewModel::changeAuthScreen,
                        modifier = Modifier.padding(contentPadding)
                    )
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
