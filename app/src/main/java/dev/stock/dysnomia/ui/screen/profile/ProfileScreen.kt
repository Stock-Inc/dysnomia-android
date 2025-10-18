package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.ui.composable.DysnomiaButton
import dev.stock.dysnomia.ui.composable.AnimatedErrorCard
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun ProfileScreen(
    profileUiState: ProfileUiState,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUserMe: Boolean = false,
    onRefresh: () -> Unit
) {
    val scrollState = rememberScrollState()

    PullToRefreshBox(
        isRefreshing = profileUiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        if (profileUiState.profile == null) {
            PlaceholderProfileScreen(
                errorMessage = profileUiState.errorMessage,
                onLogoutClick = onLogoutClick,
                isUserMe = isUserMe,
                modifier = Modifier.verticalScroll(scrollState)
            )
            return@PullToRefreshBox
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            AnimatedErrorCard(profileUiState.errorMessage)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(128.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(CircleShape)
                        .height(96.dp)
                        .aspectRatio(1.0f)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    profileUiState.profile.displayName?.let {
                        Text(
                            text = profileUiState.profile.displayName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "@${profileUiState.profile.username}",
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (isUserMe) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    DysnomiaButton(
                        text = stringResource(R.string.edit_profile),
                        onClick = onEditProfileClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    DysnomiaButton(
                        text = stringResource(R.string.logout),
                        onClick = onLogoutClick,
                        isOutlined = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Box(contentAlignment = Alignment.Center) {
                HorizontalDivider(
                    modifier = Modifier.padding(16.dp),
                    thickness = 4.dp
                )
                Text(
                    stringResource(R.string.bio),
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.padding(start = 16.dp, end = 16.dp)) {
                if (profileUiState.profile.bio.isNullOrEmpty()) {
                    Text(
                        text = profileUiState.profile.bio ?: "No bio provided",
                        modifier = Modifier.alpha(0.5f)
                    )
                } else {
                    Text(
                        text = profileUiState.profile.bio
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderProfileScreen(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isUserMe: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        AnimatedErrorCard(errorMessage)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shimmer()
                .height(128.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .height(96.dp)
                    .aspectRatio(1.0f)
                    .background(DividerDefaults.color)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterVertically
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .size(128.dp, 20.dp)
                        .background(DividerDefaults.color)
                )
            }
        }
        if (isUserMe) {
            DysnomiaButton(
                text = stringResource(R.string.logout),
                onClick = onLogoutClick,
                isOutlined = true,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        Box(contentAlignment = Alignment.Center) {
            HorizontalDivider(
                modifier = Modifier.padding(16.dp),
                thickness = 4.dp
            )
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
                    .background(DividerDefaults.color)
                    .shimmer()
                    .size(24.dp, 20.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.shimmer()
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(128.dp, 20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(64.dp, 20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ProfileScreen(
                profileUiState = ProfileUiState(
                    profile = Profile(
                        username = "alnoeralnoeralnoeralnoeralnoeralnoeralnoeralnoer",
                        displayName = "Alnoer Alnoer Alnoer Alnoer Alnoer Alnoer",
                        role = "USER",
                        bio = "some interesting info about me"
                    )
                ),
                isUserMe = true,
                onLogoutClick = {},
                onEditProfileClick = {},
                onRefresh = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ProfileScreen(
                profileUiState = ProfileUiState(
                    profile = Profile(
                        username = "alnoeralnoeralnoeralnoeralnoeralnoeralnoeralnoer",
                        displayName = null,
                        role = "USER",
                        bio = null
                    ),
                    errorMessage = "No connection with the server"
                ),
                onLogoutClick = {},
                onEditProfileClick = {},
                onRefresh = {}
            )
        }
    }
}

@Preview
@Composable
private fun PlaceholderProfileScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            PlaceholderProfileScreen(
                errorMessage = "No connection with the server",
                isUserMe = true,
                onLogoutClick = {}
            )
        }
    }
}
