package dev.stock.dysnomia.ui.screen.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composables.DysnomiaButton
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun IntroductionFirstStepScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(256.dp)
                .scale(1.5f)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = stringResource(R.string.slogan),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(32.dp))
        DysnomiaButton(
            text = stringResource(R.string.get_started),
            onClick = onGetStartedClick
        )
    }
}

@Preview
@Composable
private fun IntroductionFirstStepScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            IntroductionFirstStepScreen(
                onGetStartedClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun IntroductionFirstStepScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            IntroductionFirstStepScreen(
                onGetStartedClick = {}
            )
        }
    }
}
