package dev.stock.dysnomia.ui.screen.introduction

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composable.DysnomiaButton
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun IntroductionSecondStepScreen(
    onProceed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        Firebase.messaging.subscribeToTopic("global")
        onProceed()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(R.string.get_updates),
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = stringResource(R.string.allow_notifications_prompt),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(32.dp))
        DysnomiaButton(
            text = stringResource(R.string.allow_notifications),
            onClick = {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun IntroductionSecondStepScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            IntroductionSecondStepScreen(
                onProceed = {}
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun IntroductionSecondStepScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            IntroductionSecondStepScreen(
                onProceed = {}
            )
        }
    }
}
