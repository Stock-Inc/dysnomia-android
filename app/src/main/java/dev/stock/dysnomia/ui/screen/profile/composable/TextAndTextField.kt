package dev.stock.dysnomia.ui.screen.profile.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composable.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun TextAndTextField(
    textFieldState: TextFieldState,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onKeyboardAction: KeyboardActionHandler? = null,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    maxLines: Int = 1
) {
    Column(modifier) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(4.dp)
        )
        DysnomiaTextField(
            state = textFieldState,
            enabled = enabled,
            label = stringResource(label),
            maxLines = maxLines,
            leadingIcon = leadingIcon,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction
        )
    }
}

@Preview
@Composable
private fun TextAndTextFieldDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            TextAndTextField(
                textFieldState = TextFieldState(),
                label = R.string.display_name,
                leadingIcon = Icons.Default.Person
            )
        }
    }
}

@Preview
@Composable
private fun TextAndTextFieldLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            TextAndTextField(
                textFieldState = TextFieldState(),
                label = R.string.display_name,
                leadingIcon = Icons.Default.Person
            )
        }
    }
}
