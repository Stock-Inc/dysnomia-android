package su.femboymatrix.buttplug.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun FemboyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions(),
    leadingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction
        ),
        singleLine = true,
        keyboardActions = keyboardActions,
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
            focusedIndicatorColor = FemboyPink,
            focusedLabelColor = FemboyPink,
            focusedLeadingIconColor = FemboyPink
        ),
        modifier = modifier.fillMaxWidth()
    )
}
