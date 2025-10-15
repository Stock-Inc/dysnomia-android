package dev.stock.dysnomia.ui.composable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
    enabled: Boolean = true
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
        ) {
            Text(
                text = text,
                maxLines = 1
            )
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
        ) {
            Text(
                text = text,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun DysnomiaButtonPreview() {
    DysnomiaTheme {
        DysnomiaButton(
            text = "Login",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun DysnomiaButtonOutlinedPreview() {
    DysnomiaTheme {
        DysnomiaButton(
            text = "Login",
            isOutlined = true,
            onClick = {}
        )
    }
}
