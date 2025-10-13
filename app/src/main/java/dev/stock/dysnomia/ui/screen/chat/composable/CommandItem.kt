package dev.stock.dysnomia.ui.screen.chat.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

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

@Preview
@Composable
private fun CommandItemDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
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
private fun CommandItemLightPreview() {
    DysnomiaTheme(darkTheme = false) {
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
private fun ErrorItemDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
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
private fun ErrorItemLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        CommandItem(
            messageEntity = MessageEntity(
                message = "Error connecting to the server:\njava.lang.Exception keystrokes"
            ),
            onClick = {}
        )
    }
}
