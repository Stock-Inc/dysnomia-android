package dev.stock.dysnomia.ui.screen.chat

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.ui.composables.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.roundToInt

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeReversed = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

enum class DragValue { Replied, Resting }

@Composable
fun MessageItem(
    messageEntity: MessageEntity,
    onClick: () -> Unit,
    onReply: (MessageEntity) -> Unit,
    isUserMe: Boolean,
    isTheFirstMessageFromAuthor: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val anchors = with(density) {
        DraggableAnchors {
            DragValue.Replied at -50.dp.toPx()
            DragValue.Resting at 0f
        }
    }

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Resting,
            anchors = anchors
        )
    }

    LaunchedEffect(draggableState.settledValue) {
        if (draggableState.currentValue == DragValue.Replied) {
            onReply(messageEntity)
        }
        draggableState.animateTo(DragValue.Resting)
    }

    LaunchedEffect(draggableState.currentValue) {
        if (draggableState.currentValue == DragValue.Replied) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        }
    }

    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
        modifier = modifier
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                    state = draggableState,
                    positionalThreshold = { distance -> distance * 0.5f },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            )
            .offset { IntOffset(x = draggableState.requireOffset().roundToInt(), y = 0) }
            .fillMaxWidth()
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
fun ReplyBox(
    repliedMessage: RepliedMessage?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    LabeledBarRow(
        barColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.height(64.dp)
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            repliedMessage?.let {
                Text(
                    text = stringResource(R.string.reply_to, repliedMessage.name),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = repliedMessage.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(
            onClick = onCancel
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null // TODO: content descriptions
            )
        }
    }
}

@Composable
fun LabeledBarRow(
    barColor: Color,
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
    barCorner: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(start = 8.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(barWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(barCorner))
                .background(barColor)
        )
        Spacer(Modifier.width(contentPadding.calculateStartPadding(LayoutDirection.Ltr)))
        content()
    }
}

@Composable
fun ChatScreen(
    chatHistory: List<MessageEntity>,
    chatUiState: ChatUiState,
    messageText: TextFieldValue,
    currentName: String,
    onTextChange: (TextFieldValue) -> Unit,
    onSendMessage: () -> Unit,
    onSendCommand: () -> Unit,
    onReply: (MessageEntity) -> Unit,
    onCancelReply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val localClipboard = LocalClipboard.current
    val context = LocalContext.current
    val textFieldFocusRequester = remember { FocusRequester() }

    val isMessageACommand = messageText.text.startsWith('/')

    LaunchedEffect(chatHistory) {
        if (chatHistory.isNotEmpty()) {
            chatListState.animateScrollToItem(0)
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true,
            state = chatListState,
            modifier = Modifier.weight(1f)
        ) {
            for (index in chatHistory.indices) {
                val item = chatHistory[index]
                val nextItem = chatHistory.getOrNull(index + 1)

                item(key = item.entityId) {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically { it } + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                        modifier = Modifier.animateItem()
                    ) {
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
                                modifier = Modifier.padding(4.dp)
                            )
                        } else {
                            MessageItem(
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
                                onReply = onReply,
                                isUserMe = item.name == currentName,
                                isTheFirstMessageFromAuthor = nextItem?.name != item.name,
                                modifier = if (item.deliveryStatus == DeliveryStatus.PENDING) {
                                    Modifier
                                        .alpha(0.5f)
                                        .padding(4.dp)
                                } else {
                                    Modifier.padding(4.dp)
                                }
                            )
                        }
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

        DysnomiaTextField(
            value = messageText,
            enabled = !chatUiState.isCommandPending,
            label = if (isMessageACommand) {
                stringResource(R.string.enter_command)
            } else {
                stringResource(R.string.enter_message)
            },
            onValueChange = onTextChange,
            maxLines = 6,
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
            } else if (isMessageACommand) {
                { onSendCommand() }
            } else {
                { onSendMessage() }
            },
            modifier = if (chatUiState.isCommandPending) {
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
                chatUiState = ChatUiState(
                    repliedMessage = RepliedMessage(
                        id = 0,
                        name = "Name ".repeat(10),
                        text = "some message ".repeat(3)
                    )
                ),
                messageText = TextFieldValue(),
                currentName = "",
                onTextChange = {},
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {}
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
                        text = "some message ".repeat(3)
                    )
                ),
                messageText = TextFieldValue("Some message"),
                currentName = "",
                onTextChange = {},
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {}
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
        MessageItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = true,
            isTheFirstMessageFromAuthor = true,
            onClick = {},
            onReply = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemYoursPreview() {
    DysnomiaTheme {
        MessageItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = true,
            isTheFirstMessageFromAuthor = false,
            onClick = {},
            onReply = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemOthersFirstMessagePreview() {
    DysnomiaTheme {
        MessageItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = false,
            isTheFirstMessageFromAuthor = true,
            onClick = {},
            onReply = {}
        )
    }
}

@Preview
@Composable
private fun ChatItemOthersPreview() {
    DysnomiaTheme {
        MessageItem(
            messageEntity = MessageEntity(
                name = "Username",
                message = "some message"
            ),
            isUserMe = false,
            isTheFirstMessageFromAuthor = false,
            onClick = {},
            onReply = {}
        )
    }
}

@Preview
@Composable
private fun ReplyBoxPreview() {
    DysnomiaTheme {
        Surface {
            ReplyBox(
                repliedMessage = RepliedMessage(
                    id = 0,
                    name = "Name ".repeat(10),
                    text = "some message ".repeat(3)
                ),
                onCancel = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
