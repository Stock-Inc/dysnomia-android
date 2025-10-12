package dev.stock.dysnomia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        var firstLaunch: Boolean
        runBlocking {
            firstLaunch = preferencesRepository.firstLaunch.first()
        }

        setContent {
            DysnomiaTheme {
                DysnomiaApp(firstLaunch = firstLaunch)
            }
        }
    }
}
