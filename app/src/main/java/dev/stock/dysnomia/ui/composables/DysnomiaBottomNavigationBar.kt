package dev.stock.dysnomia.ui.composables

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
import androidx.compose.ui.tooling.preview.Preview
import dev.stock.dysnomia.DysnomiaApp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaBottomNavigationBar(
    currentScreen: DysnomiaApp,
    onClick: (DysnomiaApp) -> Unit,
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
    val screenType: DysnomiaApp,
    val icon: ImageVector,
    @StringRes val text: Int
)

val navigationItemContentList = listOf(
    NavigationItemContent(
        screenType = DysnomiaApp.Home,
        icon = Icons.Default.Home,
        text = R.string.land
    ),
    NavigationItemContent(
        screenType = DysnomiaApp.Chat,
        icon = Icons.AutoMirrored.Filled.Send,
        text = R.string.chat
    ),
    NavigationItemContent(
        screenType = DysnomiaApp.Login,
        icon = Icons.Default.Favorite,
        text = R.string.profile
    )
)

@Preview
@Composable
private fun DysnomiaBottomNavigationBarDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        DysnomiaBottomNavigationBar(
            currentScreen = DysnomiaApp.Home,
            navigationItemContentList = navigationItemContentList,
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun DysnomiaBottomNavigationBarLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        DysnomiaBottomNavigationBar(
            currentScreen = DysnomiaApp.Home,
            navigationItemContentList = navigationItemContentList,
            onClick = { }
        )
    }
}
