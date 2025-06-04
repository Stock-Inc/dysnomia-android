package dev.stock.dysnomia.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        shape = CircleShape,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (keyboardOptions.keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        label = {
            Text(label)
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedIndicatorColor = DysnomiaPink,
            focusedLabelColor = DysnomiaPink,
            focusedLeadingIconColor = DysnomiaPink,
            focusedTrailingIconColor = DysnomiaPink
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun DysnomiaTextFieldPreview() {
    DysnomiaTheme {
        Surface {
            DysnomiaTextField(
                value = TextFieldValue(),
                label = "Enter Message",
                onValueChange = { },
                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                trailingIcon = Icons.AutoMirrored.Filled.Send
            )
        }
    }
}
