package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Luxury Dark
val KikiDarkBg = Color(0xFF0C0A14)
val KikiSurfaceDark = Color(0xFF161324)
val KikiSurfaceElevated = Color(0xFF1E1A30)
val KikiSurfaceBorder = Color(0xFF2E284A)

// Holographic & Neon Beauty Accents
val KikiLilac = Color(0xFFD67BFF)
val KikiCyan = Color(0xFF5CE5E7)
val KikiRose = Color(0xFFFF729F)
val KikiMagenta = Color(0xFFFF007F)
val KikiGold = Color(0xFFFFD166)
val KikiSilver = Color(0xFFE4E4EE)
val KikiPearl = Color(0xFFF9F8FC)
val KikiTextMuted = Color(0xFFA19CBA)
val KikiSuccess = Color(0xFF06D6A0)

// Gradients
val HolographicGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFD67BFF),
        Color(0xFF5CE5E7),
        Color(0xFFFF729F)
    )
)

val ChromeGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFE8E5F0),
        Color(0xFFA5A0B8),
        Color(0xFFF0EDF8)
    )
)

val LabGlowGradient = Brush.radialGradient(
    colors = listOf(
        Color(0x33D67BFF),
        Color(0x000C0A14)
    )
)
