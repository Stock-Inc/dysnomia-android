package dev.stock.dysnomia.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun HomeScreen(
    onChatClicked: (Int) -> Unit,
    onProfileClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(4.dp)
            )
            IconButton(
                onClick = onProfileClicked
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Column {
            ChatCard(
                name = stringResource(R.string.global_chat),
                details = stringResource(R.string.chat_with_everyone_online),
                onClick = { onChatClicked(0) },
                image = R.drawable.ic_launcher_foreground
            )
        }
    }
}

@Composable
fun ChatCard(
    name: String,
    details: String,
    @DrawableRes image: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier.height(128.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = details,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Image(
                painter = painterResource(image),
                contentDescription = null,
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .scale(2f)
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            HomeScreen(
                onChatClicked = {},
                onProfileClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            HomeScreen(
                onChatClicked = {},
                onProfileClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun ChatCardDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ChatCard(
                name = "Global chat",
                details = "Chat with everyone online",
                onClick = {},
                image = R.drawable.ic_launcher_foreground
            )
        }
    }
}
