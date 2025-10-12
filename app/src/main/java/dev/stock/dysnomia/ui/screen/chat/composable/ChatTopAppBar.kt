package dev.stock.dysnomia.ui.screen.chat.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.stock.dysnomia.R
import dev.stock.dysnomia.data.ConnectionState
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopAppBar(
    connectionState: ConnectionState,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.global_chat),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AnimatedVisibility(connectionState !is ConnectionState.Connected) {
                    Text(
                        text = connectionState.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPressed
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        modifier = modifier
    )
}

@Preview
@Composable
private fun ChatTopAppBarLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ChatTopAppBar(
                connectionState = ConnectionState.Connecting,
                onBackPressed = {}
            )
        }
    }
}
@Preview
@Composable
private fun ChatTopAppBarDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ChatTopAppBar(
                connectionState = ConnectionState.Connecting,
                onBackPressed = {}
            )
        }
    }
}
