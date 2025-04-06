package dev.stock.dysnomia.ui.screen.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.theme.DysnomiaDarkPink
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.ui.theme.MysteriousPurple
import dev.stock.dysnomia.utils.grayScale

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onChatClicked: (Int) -> Unit,
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
                                colors = listOf(DysnomiaPink, MysteriousPurple)
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
        LazyColumn {
            item {
                FormListItem(
                    name = "[ Global ] chat",
                    details = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(DysnomiaPink, MysteriousPurple)
                                )
                            )
                        ) {
                            append("Find someone ♡")
                        }
                    },
                    onClick = { onChatClicked(0) },
                    image = R.drawable.astolfo,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                FormListItem(
                    name = "Astolfo [ WIP ]",
                    image = R.drawable.astolfo,
                    onClick = { onChatClicked(1) },
                    details = buildAnnotatedString {
                        append("Gender: ")
                        withStyle(
                            SpanStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(DysnomiaPink, MysteriousPurple)
                                )
                            )
                        ) {
                            append("Secret\n")
                        }
                        append("Height: 164 cm\nWeight: 56 kg")
                    },
                    modifier = Modifier.Companion.grayScale()
                )
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = DysnomiaDarkPink),
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
private fun HomeScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            HomeScreen(
                uiState = HomeUiState(),
                onChatClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            HomeScreen(
                uiState = HomeUiState(),
                onChatClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun FormListItemDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            FormListItem(
                name = "[ Global ] chat",
                details = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(DysnomiaPink, MysteriousPurple)
                            )
                        )
                    ) {
                        append("Find someone ♡")
                    }
                },
                onClick = { },
                image = R.drawable.astolfo
            )
        }
    }
}
