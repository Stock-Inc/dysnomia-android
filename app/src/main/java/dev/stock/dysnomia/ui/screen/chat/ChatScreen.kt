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
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.PopupProperties
import com.valentinilk.shimmer.shimmer
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.ui.composables.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.utils.ANONYMOUS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    modifier: Modifier = Modifier,
    repliedMessage: RepliedMessage? = null
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
                text = messageEntity.name.ifEmpty { ANONYMOUS },
                color = MaterialTheme.colorScheme.primary,
                textAlign = if (isUserMe) TextAlign.Right else null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Surface(
            onClick = onClick,
            color = if (isUserMe) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = if (isUserMe) ChatBubbleShapeReversed else ChatBubbleShape,
            border = CardDefaults.outlinedCardBorder(true),
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                repliedMessage?.let {
                    MessageReplyBox(
                        isUserMe = isUserMe,
                        repliedMessage = repliedMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = messageEntity.message,
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 4.dp,
                            end = 4.dp
                        )
                )
                Text(
                    text = getLocalTime(messageEntity.date, context),
                    modifier = Modifier
                        .alpha(0.5f)
                        .align(alignment = Alignment.End)
                )
            }
        }
    }
}

@Composable
fun MessageItemWithReply(
    getRepliedMessageStateFlow: (Int) -> StateFlow<RepliedMessage?>,
    messageEntity: MessageEntity,
    onClick: () -> Unit,
    onReply: (MessageEntity) -> Unit,
    isUserMe: Boolean,
    isTheFirstMessageFromAuthor: Boolean,
    modifier: Modifier = Modifier
) {
    val repliedMessageFlow = remember {
        getRepliedMessageStateFlow(messageEntity.replyId)
    }
    val repliedMessage = repliedMessageFlow.collectAsState().value

    MessageItem(
        messageEntity = messageEntity,
        onClick = onClick,
        onReply = onReply,
        isUserMe = isUserMe,
        isTheFirstMessageFromAuthor = isTheFirstMessageFromAuthor,
        modifier = modifier,
        repliedMessage = repliedMessage
    )
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
            color = MaterialTheme.colorScheme.primary,
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
        isUserMe = false, // TODO: Unreadable kinda, affects bar color
        modifier = modifier.height(64.dp)
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            repliedMessage?.let {
                Text(
                    text = repliedMessage.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = repliedMessage.message,
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
fun MessageReplyBox(
    repliedMessage: RepliedMessage,
    isUserMe: Boolean,
    modifier: Modifier = Modifier
) {
    LabeledBarRow(
        isUserMe = isUserMe,
        modifier = modifier.height(64.dp)
    ) {
        Column(modifier = Modifier.padding(end = 8.dp)) {
            Text(
                text = repliedMessage.name.ifEmpty { ANONYMOUS },
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isUserMe) Color.Unspecified else MaterialTheme.colorScheme.primary
            )
            Text(
                text = repliedMessage.message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun LabeledBarRow(
    isUserMe: Boolean,
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
    barCorner: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(start = 8.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isUserMe) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
            )
    ) {
        Box(
            Modifier
                .width(barWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(barCorner))
                .background(
                    if (isUserMe) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
        )
        Spacer(Modifier.width(contentPadding.calculateStartPadding(LayoutDirection.Ltr)))
        content()
    }
}

@Composable
fun CommandSuggestionItem(
    command: String,
    result: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "/$command",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(max = 128.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = result,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(0.75f)
        )
    }
}

@Composable
fun ChatScreen(
    chatHistory: List<MessageEntity>,
    chatUiState: ChatUiState,
    messageText: TextFieldValue,
    currentName: String,
    commandSuggestions: List<CommandSuggestion>,
    onTextChange: (TextFieldValue) -> Unit,
    onSendMessage: () -> Unit,
    onSendCommand: (String?) -> Unit,
    onReply: (MessageEntity) -> Unit,
    onCancelReply: () -> Unit,
    getRepliedMessageStateFlow: (Int) -> MutableStateFlow<RepliedMessage>,
    modifier: Modifier = Modifier
) {
    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val localClipboard = LocalClipboard.current
    val context = LocalContext.current
    val textFieldFocusRequester = remember { FocusRequester() }

    val isMessageACommand = messageText.text.startsWith('/')
    val filteredSuggestions by remember(
        commandSuggestions,
        messageText.text
    ) {
        derivedStateOf {
            if (isMessageACommand) {
                commandSuggestions
                    .filter { suggestion ->
                        suggestion.command.contains(
                            messageText.text.drop(1),
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
                                    textToCopy = item.message
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
                                    .animateItem()
                            } else {
                                Modifier
                                    .padding(4.dp)
                                    .animateItem()
                            }
                        )
                    } else {
                        MessageItemWithReply(
                            messageEntity = item,
                            getRepliedMessageStateFlow = getRepliedMessageStateFlow,
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
                                    .animateItem()
                            } else {
                                Modifier
                                    .padding(4.dp)
                                    .animateItem()
                            }
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
                    { onSendCommand(null) }
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
                messageText = TextFieldValue(),
                currentName = "",
                onTextChange = {},
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {},
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
                messageText = TextFieldValue("Some message"),
                currentName = "",
                onTextChange = {},
                onSendMessage = {},
                onSendCommand = {},
                onReply = {},
                onCancelReply = {},
                getRepliedMessageStateFlow = { MutableStateFlow(RepliedMessage(0, "", "")) },
                commandSuggestions = listOf(
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    ),
                    CommandSuggestion(
                        command = "help",
                        description = "some help"
                    ),
                ),
                modifier = TODO()
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
        Surface {
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
}

@Preview
@Composable
private fun ChatItemYoursFirstMessageWithLargeReplyPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message"
                ),
                isUserMe = true,
                isTheFirstMessageFromAuthor = true,
                onClick = {},
                onReply = {},
                repliedMessage = RepliedMessage(
                    id = 0,
                    name = "Name ".repeat(10),
                    message = "some message ".repeat(3)
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatItemYoursFirstLargeMessageWithSmallReplyPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message".repeat(50)
                ),
                isUserMe = true,
                isTheFirstMessageFromAuthor = true,
                onClick = {},
                onReply = {},
                repliedMessage = RepliedMessage(
                    id = 0,
                    name = "Name",
                    message = "some message"
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatItemYoursFirstMessageWithSmallReplyPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message"
                ),
                isUserMe = true,
                isTheFirstMessageFromAuthor = true,
                onClick = {},
                onReply = {},
                repliedMessage = RepliedMessage(
                    id = 0,
                    name = "Name",
                    message = "some message"
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatItemYoursPreview() {
    DysnomiaTheme {
        Surface {
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
}

@Preview
@Composable
private fun ChatItemOthersFirstMessagePreview() {
    DysnomiaTheme {
        Surface {
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
}

@Preview
@Composable
private fun ChatItemOthersFirstMessageWithReplyPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message"
                ),
                isUserMe = false,
                isTheFirstMessageFromAuthor = true,
                onClick = {},
                onReply = {},
                repliedMessage = RepliedMessage(
                    id = 0,
                    name = "Name ".repeat(10),
                    message = "some message ".repeat(3)
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatItemOthersPreview() {
    DysnomiaTheme {
        Surface {
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
                    message = "some message ".repeat(3)
                ),
                onCancel = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun CommandSuggestionItemPreview() {
    DysnomiaTheme {
        Surface {
            CommandSuggestionItem(
                command = "help",
                result = "Provides some very very very very very useful help",
                onClick = {}
            )
        }
    }
}
