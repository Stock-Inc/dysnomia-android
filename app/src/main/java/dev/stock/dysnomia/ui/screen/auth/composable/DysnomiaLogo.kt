package dev.stock.dysnomia.ui.screen.auth.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun DysnomiaLogo(modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        append("Dysnomia ")
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary
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

@Preview
@Composable
private fun DysnomiaLogoDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            DysnomiaLogo()
        }
    }
}

@Preview
@Composable
private fun DysnomiaLogoLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            DysnomiaLogo()
        }
    }
}
