package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.model.emptyProfile
import dev.stock.dysnomia.ui.screen.profile.composable.AnimatedErrorCard
import dev.stock.dysnomia.ui.screen.profile.composable.ProfileEditTopAppBar
import dev.stock.dysnomia.ui.screen.profile.composable.TextAndTextField
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun ProfileEditScreen(
    profileEditUiState: ProfileEditUiState,
    displayNameTextFieldState: TextFieldState,
    bioTextFieldState: TextFieldState,
    onChangeImage: () -> Unit,
    onSaveClick: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ProfileEditTopAppBar(
            onBackPressed = onBackPressed,
            onSaveClick = onSaveClick
        )
        Column(Modifier.padding(8.dp)) {
            AnimatedErrorCard(profileEditUiState.errorMessage)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .height(96.dp)
                        .aspectRatio(1.0f)
                        .clickable(onClick = onChangeImage)
                )
                Text(
                    text = stringResource(R.string.change_image),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onChangeImage)
                )
            }
            Spacer(Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextAndTextField(
                    textFieldState = displayNameTextFieldState,
                    label = R.string.display_name,
                    leadingIcon = Icons.Default.Person,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                TextAndTextField(
                    textFieldState = bioTextFieldState,
                    label = R.string.bio,
                    maxLines = 12,
                    leadingIcon = Icons.Default.Edit,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    }
}


@Preview
@Composable
private fun ProfileEditScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            ProfileEditScreen(
                displayNameTextFieldState = TextFieldState(),
                bioTextFieldState = TextFieldState(),
                profileEditUiState = ProfileEditUiState(
                    profile = emptyProfile
                ),
                onChangeImage = {},
                onSaveClick = {},
                onBackPressed = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfileEditScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            ProfileEditScreen(
                displayNameTextFieldState = TextFieldState(),
                bioTextFieldState = TextFieldState(),
                profileEditUiState = ProfileEditUiState(
                    profile = emptyProfile
                ),
                onChangeImage = {},
                onSaveClick = {},
                onBackPressed = {}
            )
        }
    }
}
