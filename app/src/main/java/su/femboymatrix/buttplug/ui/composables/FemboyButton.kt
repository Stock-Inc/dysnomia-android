package su.femboymatrix.buttplug.ui.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import su.femboymatrix.buttplug.ui.theme.FemboyMatrixTheme
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun FemboyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = FemboyPink
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
                containerColor = FemboyPink
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
private fun FemboyButtonPreview() {
    FemboyMatrixTheme {
        FemboyButton(
            text = "Login inside",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun FemboyButtonOutlinedPreview() {
    FemboyMatrixTheme {
        FemboyButton(
            text = "Login inside",
            isOutlined = true,
            onClick = {}
        )
    }
}