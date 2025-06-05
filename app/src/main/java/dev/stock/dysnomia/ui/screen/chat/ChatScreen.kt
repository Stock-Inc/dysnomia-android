package dev.stock.dysnomia.ui.screen.chat

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.ui.composables.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import kotlinx.coroutines.launch
import java.util.Date

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeReversed = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@Composable
fun ChatItem(
    messageEntity: MessageEntity,
    onClick: () -> Unit,
    isUserMe: Boolean,
    isTheFirstMessageFromAuthor: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isTheFirstMessageFromAuthor) {
            Text(
                text = messageEntity.name.ifEmpty {
                    "Anonymous"
                },
                color = DysnomiaPink,
                textAlign = if (isUserMe) TextAlign.Right else null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Surface(
            onClick = onClick,
            color = if (isUserMe) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = if (isUserMe) ChatBubbleShapeReversed else ChatBubbleShape,
            border = CardDefaults.outlinedCardBorder(true)
        ) {
            Column(
                horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = messageEntity.message,
                    color = if (isUserMe) MaterialTheme.colorScheme.surface else DysnomiaPink,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp
                        )
                )
                Text(
                    text = getLocalTime(messageEntity.date, context),
                    color = if (isUserMe) MaterialTheme.colorScheme.surface else DysnomiaPink,
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .align(alignment = Alignment.End)
                )
            }
        }
    }
}

private fun getLocalTime(unixTime: Long, context: Context): String {
    val formatter = DateFormat.getTimeFormat(context)
    return formatter.format(Date(unixTime * 1000))
}

@Composable
fun CommandItem(
    messageEntity: MessageEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier.fillMaxWidth()
    ) {
        Text(
            text = if (messageEntity.name != "") {
                "> ${messageEntity.name}\n${messageEntity.message}"
            } else {
                messageEntity.message
            },
            color = DysnomiaPink,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ChatScreen(
    chatHistory: List<MessageEntity>,
    messageText: TextFieldValue,
    currentName: String,
    onTextChange: (TextFieldValue) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier,
    isMessagePending: Boolean = false
) {
    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val historySize = chatHistory.size
    val localClipboard = LocalClipboard.current
    val context = LocalContext.current
    val textFieldFocusRequester = FocusRequester()

    val isMessageACommand = messageText.text.startsWith('/')

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        if (historySize != 0) {
            LaunchedEffect(historySize) {
                chatListState.scrollToItem(historySize)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            state = chatListState,
            modifier = Modifier.weight(1f)
        ) {
            for (index in chatHistory.indices) {
                val item = chatHistory[index]
                val previousItem = chatHistory.getOrNull(index - 1)

                item(key = item.entityId) {
                    if (item.isCommand) {
                        CommandItem(
                            messageEntity = item,
                            onClick = {
                                coroutineScope.launch {
                                    copyToClipboard(
                                        context = context,
                                        localClipboard = localClipboard,
                                        textToCopy = item.message
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .animateItem()
                        )
                    } else {
                        ChatItem(
                            messageEntity = item,
                            onClick = {
                                coroutineScope.launch {
                                    copyToClipboard(
                                        context = context,
                                        localClipboard = localClipboard,
                                        textToCopy = item.message
                                    )
                                }
                            },
                            isUserMe = item.name == currentName,
                            isTheFirstMessageFromAuthor = previousItem?.name != item.name,
                            modifier = Modifier
                                .padding(4.dp)
                                .animateItem()
                        )
                    }
                }
            }
        }

        DysnomiaTextField(
            value = messageText,
            enabled = !isMessagePending,
            label = if (isMessageACommand) {
                stringResource(R.string.enter_command)
            } else {
                stringResource(R.string.enter_message)
            },
            onValueChange = onTextChange,
            maxLines = if (isMessageACommand) 1 else 6,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.None
            ),
            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            trailingIcon = if (messageText.text.isEmpty()) {
                ImageVector.vectorResource(R.drawable.code)
            } else {
                ImageVector.vectorResource(R.drawable.send)
            },
            onTrailingIconClick = if (messageText.text.isEmpty()) {
                {
                    onTextChange(
                        TextFieldValue(
                            text = "/",
                            selection = TextRange(1)
                        )
                    )
                    textFieldFocusRequester.requestFocus()
                }
            } else {
                { onSendMessage() }
            },
            modifier = if (isMessagePending) {
                Modifier
                    .focusRequester(textFieldFocusRequester)
                    .shimmer()
            } else {
                Modifier.focusRequester(textFieldFocusRequester)
            }
        )
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
                messageText = TextFieldValue(),
                currentName = "",
                onTextChange = {},
                onSendMessage = {}
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
                messageText = TextFieldValue("Some message"),
                currentName = "",
                onTextChange = {},
                onSendMessage = {}
            )
        }
    }
}

@Preview
@Composable
private fun CommandItemPreview() {
    DysnomiaTheme {
        CommandItem(
            messageEntity = MessageEntity(
                name = "help",
                message = "Some helpful message"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ErrorItemPreview() {
    DysnomiaTheme {
        CommandItem(
            messageEntity = MessageEntity(
                message = "Error connecting to the server:\njava.lang.Exception keystrokes"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemYoursFirstMessagePreview() {
    DysnomiaTheme {
        ChatItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = true,
            isTheFirstMessageFromAuthor = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemYoursPreview() {
    DysnomiaTheme {
        ChatItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = true,
            isTheFirstMessageFromAuthor = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemOthersFirstMessagePreview() {
    DysnomiaTheme {
        ChatItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = false,
            isTheFirstMessageFromAuthor = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemOthersPreview() {
    DysnomiaTheme {
        ChatItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = false,
            isTheFirstMessageFromAuthor = false,
            onClick = {}
        )
    }
}
