package com.example.plugd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.plugd.R


// 1. Define your color palettes
private val LightColors = lightColorScheme(
    primary = Color(0xFFFF9800),
    onPrimary = Color.White,
    secondary = Color(0xFFD7D7D7),
    onSecondary = Color.Black,
    background = Color(0xFFF6F6F6),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,

)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF9800),
    onPrimary = Color.Black,
    secondary = Color(0xFFD7D7D7),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

// 2.Typography

// FontFamily definition
val Telegraf = FontFamily(
    Font(R.font.telegraf_ultralight, FontWeight.W200), // UltraLight
    Font(R.font.telegraf_regular, FontWeight.Normal), // Regular
    Font(R.font.telegraf_ultrabold, FontWeight.W800)  // UltraBold
)
private val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Telegraf,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Telegraf,
        fontWeight = FontWeight.W800, // UltraBold
        fontSize = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Telegraf,
        fontWeight = FontWeight.W200, // UltraLight
        fontSize = 14.sp
    )
)
// 3. Define your shapes
private val AppShapes = Shapes() // Default shapes

// 4. Create the theme composable
@Composable
fun PLUGDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}