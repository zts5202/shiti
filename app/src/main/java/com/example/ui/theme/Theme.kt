package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TemperedCyan,
    onPrimary = Color(0xFF0F172A),
    secondary = FurnaceOrange,
    onSecondary = Color.White,
    tertiary = QuenchedEmerald,
    background = ObsidianBackground,
    surface = DarkSteelSurface,
    onBackground = OnDarkWhite,
    onSurface = OnDarkWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    secondary = FurnaceOrange,
    onSecondary = Color.White,
    tertiary = Color(0xFF059669),
    background = LightPearlBackground,
    surface = LightCrystalSurface,
    onBackground = OnLightCharcoal,
    onSurface = OnLightCharcoal
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = ThemeManager.isDarkMode,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
