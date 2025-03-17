package dev.stock.dysnomia.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import dev.stock.dysnomia.ui.theme.DysnomiaPink

@Composable
fun DysnomiaLogo(modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        append("Dysnomia ")
        withStyle(
            SpanStyle(
                color = DysnomiaPink
            )
        ) {
            append("[\u00A0]")
        }
    }
    Text(
        text = text,
        fontSize = 48.sp,
        modifier = modifier
    )
}
