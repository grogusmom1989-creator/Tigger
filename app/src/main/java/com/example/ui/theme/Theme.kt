package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KikiDarkColorScheme = darkColorScheme(
    primary = KikiLilac,
    onPrimary = Color(0xFF120024),
    primaryContainer = Color(0xFF3B1F54),
    onPrimaryContainer = Color(0xFFF2D6FF),
    secondary = KikiCyan,
    onSecondary = Color(0xFF002022),
    secondaryContainer = Color(0xFF004F52),
    onSecondaryContainer = Color(0xFF9CF6F8),
    tertiary = KikiRose,
    onTertiary = Color(0xFF370014),
    tertiaryContainer = Color(0xFF5E1029),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = KikiDarkBg,
    onBackground = KikiPearl,
    surface = KikiSurfaceDark,
    onSurface = KikiPearl,
    surfaceVariant = KikiSurfaceElevated,
    onSurfaceVariant = KikiTextMuted,
    outline = KikiSurfaceBorder
)

// We maintain a sleek dark luxury mode by default to highlight holographic cosmetics
private val KikiLightColorScheme = KikiDarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KikiDarkColorScheme,
        typography = Typography,
        content = content
    )
}
