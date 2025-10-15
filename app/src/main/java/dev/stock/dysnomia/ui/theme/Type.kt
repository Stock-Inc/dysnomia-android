package dev.stock.dysnomia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.stock.dysnomia.R

val Dustismo = FontFamily(
    Font(
        R.font.dustismo,
        FontWeight.Normal
    ),
    Font(
        R.font.dustismo_bold,
        FontWeight.Bold
    ),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    displaySmall = TextStyle(
        fontSize = 36.sp,
        fontFamily = Dustismo,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontFamily = Dustismo,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontFamily = Dustismo,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Dustismo,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
