package su.femboymatrix.buttplug.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.ui.theme.FemboyDarkPink
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink
import su.femboymatrix.buttplug.ui.theme.MysteriousPurple

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.welcome))
                    withStyle(
                        SpanStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(FemboyPink, MysteriousPurple)
                            )
                        )
                    ) {
                        if (uiState.name != "") {
                            append("[ ${uiState.name} ]")
                        } else {
                            append("[ ${stringResource(R.string.your_name)} ]")
                        }
                    }
                },
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(4.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        FormListItem(
            name = "Astolfo",
            image = R.drawable.astolfo,
            onClick = {},
            details = buildAnnotatedString {
                append("Gender: ")
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(FemboyPink, MysteriousPurple)
                        )
                    )
                ) {
                    append("Secret\n")
                }
                append(
                    "Height: 164 cm\n" +
                        "Weight: 56 kg",
                )
            }
        )
    }
}

@Composable
fun FormListItem(
    name: String,
    details: AnnotatedString,
    @DrawableRes image: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = FemboyDarkPink),
        modifier = modifier.heightIn(128.dp, 256.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(Modifier.height(36.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                alignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    FemboyMatrixTheme {
        HomeScreen(HomeUiState(name = "Your Name"))
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        HomeScreen(HomeUiState(name = "Your Name"))
    }
}
