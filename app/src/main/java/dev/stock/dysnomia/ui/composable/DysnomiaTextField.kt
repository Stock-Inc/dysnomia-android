package dev.stock.dysnomia.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun DysnomiaTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onKeyboardAction: KeyboardActionHandler? = null
) {
    OutlinedTextField(
        state = state,
        enabled = enabled,
        shape = CircleShape,
        lineLimits = if (maxLines == 1) {
            TextFieldLineLimits.SingleLine
        } else {
            TextFieldLineLimits.MultiLine(maxLines)
        },
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
        trailingIcon = {
            if (trailingIcon != null) {
                IconButton(
                    enabled = enabled,
                    onClick = onTrailingIconClick
                ) {
                    Icon(
                        trailingIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                    )
                }
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
private fun DysnomiaTextFieldDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            DysnomiaTextField(
                state = TextFieldState(),
                label = "Enter Message",
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                trailingIcon = Icons.AutoMirrored.Filled.Send
            )
        }
    }
}

@Preview
@Composable
private fun DysnomiaTextFieldLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            DysnomiaTextField(
                state = TextFieldState(),
                label = "Enter Message",
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                trailingIcon = Icons.AutoMirrored.Filled.Send
            )
        }
    }
}
