package dev.stock.dysnomia.ui.screen.chat.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.ui.theme.DysnomiaTheme
import dev.stock.dysnomia.utils.ANONYMOUS

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

@Preview
@Composable
private fun ReplyBoxDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
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
private fun ReplyBoxLightPreview() {
    DysnomiaTheme(darkTheme = false) {
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
