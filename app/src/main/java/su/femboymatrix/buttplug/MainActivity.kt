package su.femboymatrix.buttplug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FemboyMatrixTheme {
                FemboyApp()
            }
        }
    }
}