package dev.stock.dysnomia.ui.screen.chat.composable

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.ui.screen.chat.DragValue
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.utils.ANONYMOUS
import dev.stock.dysnomia.utils.setVisualsBasedOfMessageStatus
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import kotlin.math.roundToInt

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeReversed = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

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
            .setVisualsBasedOfMessageStatus(messageEntity.deliveryStatus)
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
private fun ChatItemYoursFirstMessageWithSmallReplyPendingPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message",
                    deliveryStatus = DeliveryStatus.PENDING
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
private fun ChatItemYoursFirstMessageWithSmallReplyFailedPreview() {
    DysnomiaTheme {
        Surface {
            MessageItem(
                messageEntity = MessageEntity(
                    name = "Username",
                    message = "some message",
                    deliveryStatus = DeliveryStatus.FAILED
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
