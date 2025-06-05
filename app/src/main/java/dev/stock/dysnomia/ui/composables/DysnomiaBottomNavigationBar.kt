package dev.stock.dysnomia.ui.composables

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import dev.stock.dysnomia.DysnomiaApp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaBottomNavigationBar(
    currentScreen: DysnomiaApp,
    onClick: (DysnomiaApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationItemContentList = listOf(
        NavigationItemContent(
            screenType = DysnomiaApp.Home,
            icon = ImageVector.vectorResource(R.drawable.home),
            text = R.string.land
        ),
        NavigationItemContent(
            screenType = DysnomiaApp.Chat,
            icon = ImageVector.vectorResource(R.drawable.send),
            text = R.string.chat
        ),
        NavigationItemContent(
            screenType = DysnomiaApp.Profile,
            icon = ImageVector.vectorResource(R.drawable.heart),
            text = R.string.profile
        )
    )

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

@Preview
@Composable
private fun DysnomiaBottomNavigationBarDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        DysnomiaBottomNavigationBar(
            currentScreen = DysnomiaApp.Home,
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
            onClick = { }
        )
    }
}
