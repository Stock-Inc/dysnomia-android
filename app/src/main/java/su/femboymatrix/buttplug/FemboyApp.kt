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
import su.femboymatrix.buttplug.ui.screen.LoginScreen
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme

enum class Screens {
    Home, Login, Console
}

@Composable
fun FemboyApp(
    viewModel: FemboyViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screens.valueOf(
        backStackEntry?.destination?.route ?: Screens.Home.name
    )

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
            startDestination = Screens.Home.name,
        ) {
            composable(route = Screens.Home.name) {
                HomeScreen(modifier = Modifier.padding(contentPadding))
            }

            composable(route = Screens.Console.name) {
                val consoleUiState = viewModel.consoleUiState.collectAsState().value
                val consoleHistory = viewModel.consoleHistory.collectAsState(emptyList()).value
                ConsoleScreen(
                    uiState = consoleUiState,
                    consoleHistory = consoleHistory,
                    onTextChange = viewModel::changeConsoleText,
                    onSendCommand = viewModel::sendCommand,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            composable(route = Screens.Login.name) {
                LoginScreen(
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