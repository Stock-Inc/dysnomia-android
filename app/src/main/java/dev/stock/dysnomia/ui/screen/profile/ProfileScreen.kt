package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composables.DysnomiaButton
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.ui.theme.MysteriousPurple

@Composable
fun ProfileScreen(
    name: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.you_re_currently_logged_in_as))
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(DysnomiaPink, MysteriousPurple)
                        )
                    )
                ) {
                    append(name)
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        DysnomiaButton(
            text = stringResource(R.string.logout),
            isOutlined = true,
            onClick = onLogoutClick
        )
    }
}

@Preview
@Composable
private fun ProfileScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ProfileScreen(
                name = "Your Name",
                onLogoutClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ProfileScreen(
                name = "Your Name",
                onLogoutClick = {}
            )
        }
    }
}
