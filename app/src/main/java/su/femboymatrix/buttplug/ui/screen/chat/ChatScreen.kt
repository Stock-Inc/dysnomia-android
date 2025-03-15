package su.femboymatrix.buttplug.ui.screen.chat

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.data.ChatHistoryEntity
import su.femboymatrix.buttplug.ui.composables.FemboyTextField
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeReversed = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@Composable
fun ChatItem(
    chatHistoryEntity: ChatHistoryEntity,
    onClick: () -> Unit,
    isUserMe: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = if (chatHistoryEntity.name.isNotEmpty()) {
                chatHistoryEntity.name
            } else {
                "Anonymous"
            },
            color = FemboyPink,
            textAlign = if (isUserMe) TextAlign.Right else null,
            modifier = Modifier.fillMaxWidth()
        )
        Surface(
            onClick = onClick,
            color = if (isUserMe) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = if (isUserMe) ChatBubbleShapeReversed else ChatBubbleShape,
            border = CardDefaults.outlinedCardBorder(true)
        ) {
            Text(
                text = chatHistoryEntity.message,
                color = if (isUserMe) MaterialTheme.colorScheme.surface else FemboyPink,
                textAlign = if (chatHistoryEntity.message.length < 6) {
                    TextAlign.Center
                } else {
                    null
                },
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(min = 32.dp)
            )
        }
    }
}

@Composable
fun CommandItem(
    chatHistoryEntity: ChatHistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier.fillMaxWidth()
    ) {
        Text(
            text = if (chatHistoryEntity.name != "") {
                "> ${chatHistoryEntity.name}\n${chatHistoryEntity.message}"
            } else {
                chatHistoryEntity.message
            },
            color = FemboyPink,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    chatHistory: List<ChatHistoryEntity>,
    currentName: String,
    onTextChange: (String) -> Unit,
    onSendCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chatListState = rememberLazyListState()
    val historySize = chatHistory.size
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        if (historySize != 0) {
            LaunchedEffect(historySize) {
                chatListState.animateScrollToItem(historySize)
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            state = chatListState,
            modifier = Modifier.weight(1f)
        ) {
            items(chatHistory, key = { it.id }) {
                if (it.isCommand) {
                    CommandItem(
                        chatHistoryEntity = it,
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(it.message)
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
                } else {
                    ChatItem(
                        chatHistoryEntity = it,
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(it.message)
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        isUserMe = it.name == currentName,
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
        }
        FemboyTextField(
            value = uiState.text,
            label = stringResource(R.string.enter_message),
            onValueChange = onTextChange,
            imeAction = ImeAction.Send,
            keyboardActions = KeyboardActions(
                onSend = { onSendCommand() }
            ),
            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
        )
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    FemboyMatrixTheme {
        ChatScreen(
            uiState = ChatUiState(),
            chatHistory = emptyList(),
            currentName = "",
            onTextChange = {},
            onSendCommand = {}
        )
    }
}

@Preview
@Composable
private fun ChatScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        ChatScreen(
            uiState = ChatUiState(),
            chatHistory = emptyList(),
            currentName = "",
            onTextChange = {},
            onSendCommand = {}
        )
    }
}

@Preview
@Composable
private fun CommandItemPreview() {
    FemboyMatrixTheme {
        CommandItem(
            chatHistoryEntity = ChatHistoryEntity(
                message = "some message"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemPreview() {
    FemboyMatrixTheme {
        ChatItem(
            chatHistoryEntity = ChatHistoryEntity(
                name = "Alnoer",
                message = "j"
            ),
            isUserMe = true,
            onClick = {}
        )
    }
}
