package dev.stock.dysnomia

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.stock.dysnomia.ui.composables.DysnomiaBottomNavigationBar
import dev.stock.dysnomia.ui.composables.navigationItemContentList
import dev.stock.dysnomia.ui.screen.chat.ChatScreen
import dev.stock.dysnomia.ui.screen.chat.ChatViewModel
import dev.stock.dysnomia.ui.screen.home.HomeScreen
import dev.stock.dysnomia.ui.screen.home.HomeUiState
import dev.stock.dysnomia.ui.screen.profile.LoginScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileScreen
import dev.stock.dysnomia.ui.screen.profile.ProfileViewModel
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

enum class DysnomiaApp {
    Home, Login, Chat
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

    val chatHistory = chatViewModel.chatHistory.collectAsState(emptyList()).value
    val currentName = profileViewModel.currentName.collectAsState().value

    Scaffold(
        bottomBar = {
            DysnomiaBottomNavigationBar(
                currentScreen = currentScreen,
                onClick = { navController.navigate(it.name) },
                navigationItemContentList = navigationItemContentList,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = DysnomiaApp.Home.name,
        ) {
            composable(route = DysnomiaApp.Home.name) {
//                val homeUiState = homeViewModel.uiState.collectAsState().value
                val homeUiState = HomeUiState(currentName)

                HomeScreen(
                    uiState = homeUiState,
                    onChatClicked = {
                        if (it == 0) {
                            navController.navigate(DysnomiaApp.Chat.name)
                        }
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = DysnomiaApp.Chat.name) {
                val chatUiState = chatViewModel.chatUiState.collectAsState().value

                ChatScreen(
                    uiState = chatUiState,
                    chatHistory = chatHistory,
                    currentName = currentName,
                    onTextChange = chatViewModel::changeChatText,
                    onSendMessage = {
                        chatViewModel.sendMessage(
                            currentName = currentName,
                            message = chatUiState.messageText.text.trim()
                        )
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = DysnomiaApp.Login.name) {
                val loginUiState = profileViewModel.uiState.collectAsState().value

                if (currentName == "") {
                    LoginScreen(
                        uiState = loginUiState,
                        onNameChange = profileViewModel::changeName,
                        onPasswordChange = profileViewModel::changePassword,
                        onLoginClick = { profileViewModel.login(loginUiState.name.text) },
                        onRegisterClick = { profileViewModel.login(loginUiState.name.text) },
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

@Preview
@Composable
private fun DysnomiaAppPreview() {
    DysnomiaTheme(darkTheme = true) {
        DysnomiaApp()
    }
}
