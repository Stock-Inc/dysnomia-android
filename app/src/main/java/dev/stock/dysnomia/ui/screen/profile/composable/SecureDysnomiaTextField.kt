package dev.stock.dysnomia.ui.screen.profile.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SecureDysnomiaTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onKeyboardAction: KeyboardActionHandler? = null
) {
    OutlinedSecureTextField(
        state = state,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null
                )
            }
        },
        label = {
            Text(label)
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun SecureDysnomiaTextFieldDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            SecureDysnomiaTextField(
                state = TextFieldState(),
                label = "Enter Password",
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            )
        }
    }
}

@Preview
@Composable
private fun SecureDysnomiaTextFieldLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            SecureDysnomiaTextField(
                state = TextFieldState("password"),
                label = "Enter Password",
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            )
        }
    }
}
