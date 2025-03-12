package su.femboymatrix.buttplug

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
import su.femboymatrix.buttplug.ui.composables.FemboyBottomNavigationBar
import su.femboymatrix.buttplug.ui.composables.navigationItemContentList
import su.femboymatrix.buttplug.ui.screen.ConsoleScreen
import su.femboymatrix.buttplug.ui.screen.FemboyViewModel
import su.femboymatrix.buttplug.ui.screen.HomeScreen
import su.femboymatrix.buttplug.ui.screen.login.LoginScreen
import su.femboymatrix.buttplug.ui.screen.login.LoginViewModel
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme

enum class FemboyApp {
    Home, Login, Console
}

@Composable
fun FemboyApp(
    femboyViewModel: FemboyViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FemboyApp.valueOf(
        backStackEntry?.destination?.route ?: FemboyApp.Home.name
    )

    val consoleUiState = femboyViewModel.consoleUiState.collectAsState().value
    val consoleHistory = femboyViewModel.consoleHistory.collectAsState(emptyList()).value
    val loginUiState = loginViewModel.uiState.collectAsState().value

    Scaffold(
        bottomBar = {
            FemboyBottomNavigationBar(
                currentScreen = currentScreen,
                onClick = { navController.navigate(it.name) },
                navigationItemContentList = navigationItemContentList,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = FemboyApp.Home.name,
        ) {
            composable(route = FemboyApp.Home.name) {
                HomeScreen(modifier = Modifier.padding(contentPadding))
            }

            composable(route = FemboyApp.Console.name) {
                ConsoleScreen(
                    uiState = consoleUiState,
                    consoleHistory = consoleHistory,
                    onTextChange = femboyViewModel::changeConsoleText,
                    onSendCommand = femboyViewModel::sendCommand,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = FemboyApp.Login.name) {
                LoginScreen(
                    uiState = loginUiState,
                    onNameChange = loginViewModel::changeName,
                    onPasswordChange = loginViewModel::changePassword,
                    onLoginClick = {},
                    onRegisterClick = {},
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    }
}

@Preview
@Composable
private fun FemboyAppPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        FemboyApp()
    }
}
