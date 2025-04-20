package dev.stock.dysnomia.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composables.DysnomiaButton
import dev.stock.dysnomia.ui.composables.DysnomiaLogo
import dev.stock.dysnomia.ui.composables.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onNameChange: (TextFieldValue) -> Unit,
    onPasswordChange: (TextFieldValue) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DysnomiaLogo(modifier = Modifier.padding(16.dp))
        DysnomiaTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.login),
            leadingIcon = Icons.Default.AccountCircle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )
        DysnomiaTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            leadingIcon = Icons.Default.Favorite,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onLoginClick() }
            ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            DysnomiaButton(
                text = stringResource(R.string.sign_in),
                onClick = onLoginClick,
                modifier = Modifier.weight(1.0f)
            )
            Spacer(Modifier.width(32.dp))
            DysnomiaButton(
                text = stringResource(R.string.take_pink_pill),
                isOutlined = true,
                onClick = onRegisterClick,
                modifier = Modifier.weight(1.0f)
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            LoginScreen(
                uiState = LoginUiState(),
                onNameChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            LoginScreen(
                uiState = LoginUiState(),
                onNameChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }
    }
}
