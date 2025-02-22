package su.femboymatrix.buttplug

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import su.femboymatrix.buttplug.ui.screen.FemboyViewModel
import su.femboymatrix.buttplug.ui.screen.LoginScreen
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme

enum class Screens {
    Home, Login
}

@Composable
fun FemboyApp(
    viewModel: FemboyViewModel = viewModel(factory = FemboyViewModel.Factory)
) {
    NavHost(
        navController = rememberNavController(),
        startDestination = Screens.Login.name,
    ) {
        composable(route = Screens.Home.name) {

        }

        composable(route = Screens.Login.name) {
            LoginScreen(
                onLoginClick = { viewModel.sendCommand(command = "help") },
                onRegisterClick = {}
            )
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