package dev.stock.dysnomia.ui.screen.chat

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.ui.composable.DysnomiaTextField
import dev.stock.dysnomia.ui.screen.chat.composable.ChatTopAppBar
import dev.stock.dysnomia.ui.screen.chat.composable.CommandItem
import dev.stock.dysnomia.ui.screen.chat.composable.CommandSuggestionItem
import dev.stock.dysnomia.ui.screen.chat.composable.MessageItem
import dev.stock.dysnomia.ui.screen.chat.composable.MessageItemWithReply
import dev.stock.dysnomia.ui.screen.chat.composable.ReplyBox
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.utils.shimmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class DragValue { Replied, Resting }

@Composable
fun ChatScreen(
    chatHistory: List<MessageEntity>,
    chatUiState: ChatUiState,
    messageTextFieldState: TextFieldState,
    currentName: String,
    commandSuggestions: List<CommandSuggestion>,
    onSendMessage: () -> Unit,
    onSendCommand: (String?) -> Unit,
    onReply: (MessageEntity) -> Unit,
    onCancelReply: () -> Unit,
    onNavigateUp: () -> Unit,
    getRepliedMessageStateFlow: (Int) -> MutableStateFlow<RepliedMessage>,
    modifier: Modifier = Modifier
) {
    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val localClipboard = LocalClipboard.current
    val context = LocalContext.current
    val textFieldFocusRequester = remember { FocusRequester() }

    val isMessageACommand = messageTextFieldState.text.startsWith('/')
    val filteredSuggestions by remember(
        commandSuggestions,
        messageTextFieldState.text
    ) {
        derivedStateOf {
            if (isMessageACommand) {
                commandSuggestions
                    .filter { suggestion ->
                        suggestion.command.contains(
                            messageTextFieldState.text.drop(1),
                            ignoreCase = true
                        )
                    }
            } else {
                emptyList()
            }
        }
    }

    LaunchedEffect(chatHistory) {
        if (chatHistory.isNotEmpty()) {
            chatListState.animateScrollToItem(0)
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxSize()
    ) {
        ChatTopAppBar(
            connectionState = chatUiState.connectionState,
            onBackPressed = onNavigateUp
        )
        Column(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                reverseLayout = true,
                state = chatListState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = chatHistory,
                    key = { _, item -> item.entityId }
                ) { index, item ->
                    val nextItem = chatHistory.getOrNull(index + 1)

                    if (item.isCommand) {
                        CommandItem(
                            messageEntity = item,
                            onClick = {
                                coroutineScope.launch {
                                    copyToClipboard(
                                        context = context,
                                        localClipboard = localClipboard,
                                        textToCopy = "${item.name}\n${item.message}"
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .animateItem()
                        )
                    } else {
                        if (item.replyId == 0) {
                            MessageItem(
                                messageEntity = item,
                                onCopy = {
                                    coroutineScope.launch {
                                        copyToClipboard(
                                            context = context,
                                            localClipboard = localClipboard,
                                            textToCopy = it.message
                                        )
                                    }
                                },
                                onReply = onReply,
                                isUserMe = item.name == currentName,
                                isTheFirstMessageFromAuthor = nextItem?.name != item.name,
                                modifier = Modifier.animateItem()
                            )
                        } else {
                            MessageItemWithReply(
                                messageEntity = item,
                                getRepliedMessageStateFlow = getRepliedMessageStateFlow,
                                onCopy = {
                                    coroutineScope.launch {
                                        copyToClipboard(
                                            context = context,
                                            localClipboard = localClipboard,
                                            textToCopy = it.message
                                        )
                                    }
                                },
                                onReply = onReply,
                                isUserMe = item.name == currentName,
                                isTheFirstMessageFromAuthor = nextItem?.name != item.name,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = chatUiState.repliedMessage != null,
                enter = slideInVertically { it } + expandVertically(),
                exit = slideOutVertically { it } + shrinkVertically()
            ) {
                ReplyBox(
                    repliedMessage = chatUiState.repliedMessage,
                    onCancel = onCancelReply
                )
            }

            Box {
                DropdownMenu(
                    expanded = filteredSuggestions.isNotEmpty() && !chatUiState.isCommandPending,
                    onDismissRequest = { },
                    properties = PopupProperties(
                        focusable = false,
                        dismissOnClickOutside = false
                    ),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredSuggestions
                        .forEach {
                            CommandSuggestionItem(
                                command = it.command,
                                result = it.description ?: "",
                                onClick = { onSendCommand("/${it.command}") }
                            )
                        }
                }

                DysnomiaTextField(
                    state = messageTextFieldState,
                    enabled = !chatUiState.isCommandPending,
                    label = if (isMessageACommand) {
                        stringResource(R.string.enter_command)
                    } else {
                        stringResource(R.string.enter_message)
                    },
                    maxLines = 6,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.None
                    ),
                    leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    trailingIcon = if (messageTextFieldState.text.isEmpty()) {
                        ImageVector.vectorResource(R.drawable.code)
                    } else {
                        ImageVector.vectorResource(R.drawable.send)
                    },
                    onTrailingIconClick = if (messageTextFieldState.text.isEmpty()) { // FIXME
                        {
                            messageTextFieldState.setTextAndPlaceCursorAtEnd("/")
                            textFieldFocusRequester.requestFocus()
                        }
                    } else if (isMessageACommand) {
                        { onSendCommand(null) }
                    } else {
                        { onSendMessage() }
                    },
                    modifier = Modifier
                        .focusRequester(textFieldFocusRequester)
                        .shimmer(chatUiState.isCommandPending)
                )
            }
        }
    }
}

private suspend fun copyToClipboard(
    context: Context,
    localClipboard: Clipboard,
    textToCopy: String
) {
    localClipboard.setClipEntry(
        clipEntry = ClipEntry(
            clipData = ClipData.newPlainText(
                "Message",
                textToCopy
            )
        )
    )
    // Only show a toast for Android 12 and lower.
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Toast.makeText(
            context,
            context.getString(R.string.copied_to_clipboard),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview
@Composable
private fun ChatScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ChatScreen(
                chatHistory = emptyList(),
                chatUiState = ChatUiState(
                    repliedMessage = RepliedMessage(
                        id = 0,
                        name = "Name ".repeat(10),
                        message = "some message ".repeat(3)
                    )
                ),
                commandSuggestions = listOf(
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    ),
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    )
                ),
                messageTextFieldState = TextFieldState(),
                currentName = "",
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {},
                onNavigateUp = {},
                getRepliedMessageStateFlow = { MutableStateFlow(RepliedMessage(0, "", "")) }
            )
        }
    }
}

@Preview
@Composable
private fun ChatScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ChatScreen(
                chatHistory = emptyList(),
                chatUiState = ChatUiState(
                    repliedMessage = RepliedMessage(
                        id = 0,
                        name = "Name ".repeat(10),
                        message = "some message ".repeat(3)
                    )
                ),
                messageTextFieldState = TextFieldState("Some message"),
                currentName = "",
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {},
                onNavigateUp = {},
                getRepliedMessageStateFlow = { MutableStateFlow(RepliedMessage(0, "", "")) },
                commandSuggestions = listOf(
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    ),
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    )
                )
            )
        }
    }
}
