package su.femboymatrix.buttplug.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.femboymatrix.buttplug.R
import su.femboymatrix.buttplug.ui.composables.FemboyButton
import su.femboymatrix.buttplug.ui.composables.FemboyLogo
import su.femboymatrix.buttplug.ui.composables.FemboyTextField
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun LoginScreen(
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
        Spacer(Modifier.weight(1.0f))
        FemboyLogo(modifier = Modifier.padding(16.dp))
        FemboyTextField(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.login),
            leadingIcon = Icons.Default.AccountCircle,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )
        FemboyTextField(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.password),
            leadingIcon = Icons.Default.Favorite,
            imeAction = ImeAction.Done,
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
            FemboyButton(
                text = stringResource(R.string.sign_in),
                onClick = onLoginClick,
                modifier = Modifier.weight(1.0f)
            )
            Spacer(Modifier.width(32.dp))
            FemboyButton(
                text = stringResource(R.string.take_pink_pill),
                isOutlined = true,
                onClick = onRegisterClick,
                modifier = Modifier.weight(1.0f)
            )
        }
        Spacer(Modifier.weight(1.0f))
        Text(
            text = stringResource(R.string.powered_by_monster_energy_drink),
            color = FemboyPink,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.alpha(0.8f)
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    FemboyMatrixTheme {
        LoginScreen(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview
@Composable
private fun LoginScreenDarkPreview() {
    FemboyMatrixTheme(darkTheme = true) {
        LoginScreen(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}