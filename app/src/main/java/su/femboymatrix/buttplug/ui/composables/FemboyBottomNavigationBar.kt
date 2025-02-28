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
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.Screens

@Composable
fun FemboyBottomNavigationBar(
    currentScreen: Screens,
    onClick: (Screens) -> Unit,
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
    val screenType: Screens,
    val icon: ImageVector,
    @StringRes val text: Int
)

val navigationItemContentList = listOf(
    NavigationItemContent(
        screenType = Screens.Home,
        icon = Icons.Default.Home,
        text = R.string.land
    ),
    NavigationItemContent(
        screenType = Screens.Console,
        icon = Icons.AutoMirrored.Filled.Send,
        text = R.string.chat
    ),
    NavigationItemContent(
        screenType = Screens.Login,
        icon = Icons.Default.Favorite,
        text = R.string.profile
    )
)