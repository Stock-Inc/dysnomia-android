package su.femboymatrix.buttplug.ui.composables

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import su.femboymatrix.buttplug.FemboyApp
import su.femboymatrix.buttplug.R

@Composable
fun FemboyBottomNavigationBar(
    currentScreen: FemboyApp,
    onClick: (FemboyApp) -> Unit,
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentScreen == navItem.screenType,
                onClick = { onClick(navItem.screenType) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = stringResource(id = navItem.text)
                    )
                },
                label = {
                    Text(stringResource(navItem.text))
                }
            )
        }
    }
}

data class NavigationItemContent(
    val screenType: FemboyApp,
    val icon: ImageVector,
    @StringRes val text: Int
)

val navigationItemContentList = listOf(
    NavigationItemContent(
        screenType = FemboyApp.Home,
        icon = Icons.Default.Home,
        text = R.string.land
    ),
    NavigationItemContent(
        screenType = FemboyApp.Chat,
        icon = Icons.AutoMirrored.Filled.Send,
        text = R.string.chat
    ),
    NavigationItemContent(
        screenType = FemboyApp.Login,
        icon = Icons.Default.Favorite,
        text = R.string.profile
    )
)
