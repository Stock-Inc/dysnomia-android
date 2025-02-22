package su.femboymatrix.buttplug.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.ui.composables.FemboyTextField
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme

@Composable
fun ConsoleScreen(
    uiState: ConsoleUiState,
    onTextChange: (String) -> Unit,
    onSendCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier
                .padding(8.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.commandHistory) {
                    Text(it)
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
        ConsoleScreen(ConsoleUiState(), {}, {})
    }
}
@Preview
@Composable
private fun ConsoleScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        ConsoleScreen(ConsoleUiState(), {}, {})
    }
}