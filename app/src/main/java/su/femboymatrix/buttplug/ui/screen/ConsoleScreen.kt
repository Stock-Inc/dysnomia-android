package su.femboymatrix.buttplug.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.data.ConsoleHistoryEntity
import su.femboymatrix.buttplug.ui.composables.FemboyTextField
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun ConsoleItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = FemboyPink,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ConsoleScreen(
    uiState: ConsoleUiState,
    consoleHistory: List<ConsoleHistoryEntity>,
    onTextChange: (String) -> Unit,
    onSendCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val consoleListState = rememberLazyListState()
    val historySize = consoleHistory.size
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier
                .padding(8.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            if (historySize != 0) {
                LaunchedEffect(historySize) {
                    consoleListState.animateScrollToItem(historySize)
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                state = consoleListState,
                modifier = Modifier.weight(1f)
            ) {
                items(consoleHistory, key = { it.id }) {
                    ConsoleItem(
                        text = "> ${it.command}\n${it.result}",
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(it.result)
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                            )
                    )
                }
            }
            FemboyTextField(
                value = uiState.text,
                label = stringResource(R.string.enter_command),
                onValueChange = onTextChange,
                imeAction = ImeAction.Send,
                keyboardActions = KeyboardActions(
                    onSend = { onSendCommand() }
                ),
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
            )
        }
    }
}

@Preview
@Composable
private fun ConsoleScreenPreview() {
    FemboyMatrixTheme {
        ConsoleScreen(ConsoleUiState(), emptyList(), {}, {})
    }
}
@Preview
@Composable
private fun ConsoleScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        ConsoleScreen(ConsoleUiState(), emptyList(), {}, {})
    }
}

@Preview
@Composable
private fun ConsoleItemPreview() {
    FemboyMatrixTheme {
        ConsoleItem("some text", {})
    }
}