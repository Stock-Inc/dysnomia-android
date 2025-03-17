package dev.stock.dysnomia.ui.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.stock.dysnomia.ui.theme.DysnomiaPink
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = DysnomiaPink
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
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = DysnomiaPink
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
