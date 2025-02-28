package su.femboymatrix.buttplug.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(FemboyPink, Blue)
                        )
                    )
                ) {
                    append(stringResource(R.string.welcome))
                }
                append("Your Name")
            },
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    FemboyMatrixTheme {
        HomeScreen()
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        HomeScreen()
    }
}