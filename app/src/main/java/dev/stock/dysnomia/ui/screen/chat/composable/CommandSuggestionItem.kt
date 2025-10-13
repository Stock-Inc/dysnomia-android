package dev.stock.dysnomia.ui.screen.chat.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

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

@Preview
@Composable
private fun CommandSuggestionItemDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            CommandSuggestionItem(
                command = "help",
                result = "Provides some very very very very very useful help",
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun CommandSuggestionItemLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            CommandSuggestionItem(
                command = "help",
                result = "Provides some very very very very very useful help",
                onClick = {}
            )
        }
    }
}
