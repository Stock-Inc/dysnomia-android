package dev.stock.dysnomia.ui.screen.profile.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditTopAppBar(
    onBackPressed: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPressed
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            Text(
                text = stringResource(R.string.save_changes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = onSaveClick)
            )
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        modifier = modifier
    )
}

@Preview
@Composable
private fun ProfileEditTopAppBarLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ProfileEditTopAppBar(
                onBackPressed = {},
                onSaveClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfileEditTopAppBarDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ProfileEditTopAppBar(
                onBackPressed = {},
                onSaveClick = {}
            )
        }
    }
}
