package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MJGarmentsColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = TextPrimary,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = CharcoalDark,
    onSecondary = Color.White,
    secondaryContainer = WarmCreamSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = SapphireLiquid,
    onTertiary = Color.White,
    tertiaryContainer = SapphireLiquidBg,
    onTertiaryContainer = SapphireLiquid,
    background = WarmCreamBg,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = WarmCreamSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderGold,
    outlineVariant = Color(0xFFEADBBE),
    error = RoseLiquid,
    onError = Color.White,
    errorContainer = RoseLiquidBg,
    onErrorContainer = RoseLiquid
)

@Composable
fun MJGarmentsTheme(
    darkTheme: Boolean = false, // Strictly Light Mode as per user brand spec (No Dark Mode anywhere)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MJGarmentsColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MJGarmentsTheme(darkTheme, dynamicColor, content)
}
