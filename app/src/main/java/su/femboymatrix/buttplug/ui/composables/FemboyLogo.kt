package su.femboymatrix.buttplug.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import su.femboymatrix.buttplug.ui.theme.FemboyPink

@Composable
fun FemboyLogo(modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = FemboyPink
            )
        ) {
            append("femboy")
        }
        append("matrix")
        withStyle(
            SpanStyle(
                color = FemboyPink,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("[ ]")
        }
    }
    Text(
        text = text,
        fontSize = 48.sp,
        modifier = modifier
    )
}