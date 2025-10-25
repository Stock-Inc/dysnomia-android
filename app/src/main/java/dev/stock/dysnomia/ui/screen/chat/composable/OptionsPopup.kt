package dev.stock.dysnomia.ui.screen.chat.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

data class Option(
    val icon: ImageVector,
    val text: Int = 0,
    val onClick: () -> Unit
)

@Composable
fun OptionsPopup(
    expanded: Boolean,
    options: List<Option>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(128.dp, (-128).dp),
        modifier = modifier
    ) {
        options.forEach {
            OptionItem(it)
        }
    }
}

@Composable
fun OptionItem(
    option: Option,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(option.text),
                fontWeight = FontWeight.Bold
            )
        },
        leadingIcon = {
            Icon(
                imageVector = option.icon,
                contentDescription = null
            )
        },
        onClick = option.onClick,
        modifier = modifier
    )
}

private val previewOptions = listOf(
    Option(
        icon = Icons.Default.ContentCopy,
        text = R.string.copy,
        onClick = {}
    ),
    Option(
        icon = Icons.AutoMirrored.Filled.Reply,
        text = R.string.reply,
        onClick = {}
    )
)

@Preview
@Composable
private fun OptionsPopupWithMessageDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Column {
            Box {
                MessageItem(
                    messageEntity = MessageEntity(
                        name = "Username",
                        message = "some message"
                    ),
                    isUserMe = true,
                    isTheFirstMessageFromAuthor = true,
                    repliedMessage = RepliedMessage(
                        id = 0,
                        name = "Name",
                        message = "some message"
                    )
                )
                OptionsPopup(
                    expanded = true,
                    options = previewOptions,
                    onDismiss = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionsPopupWithMessageLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            Box {
                MessageItem(
                    messageEntity = MessageEntity(
                        name = "Username",
                        message = "some message"
                    ),
                    isUserMe = false,
                    isTheFirstMessageFromAuthor = true,
                    repliedMessage = RepliedMessage(
                        id = 0,
                        name = "Name",
                        message = "some message"
                    )
                )
                OptionsPopup(
                    expanded = true,
                    options = previewOptions,
                    onDismiss = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionItemDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            OptionItem(previewOptions[0])
        }
    }
}

@Preview
@Composable
private fun OptionItemLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            OptionItem(previewOptions[0])
        }
    }
}
