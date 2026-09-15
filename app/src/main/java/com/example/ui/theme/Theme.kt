package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PoseXDarkColorScheme = darkColorScheme(
    primary = PoseXElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B185F),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = PoseXNeonPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C0519),
    onSecondaryContainer = Color(0xFFFFE4E6),
    tertiary = PoseXCyan,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF083344),
    onTertiaryContainer = Color(0xFFCFFAFE),
    background = PoseXDarkBackground,
    onBackground = PoseXTextPrimary,
    surface = PoseXDarkSurface,
    onSurface = PoseXTextPrimary,
    surfaceVariant = PoseXDarkSurfaceVariant,
    onSurfaceVariant = PoseXTextSecondary,
    outline = PoseXTextMuted,
    outlineVariant = PoseXBorder
)

// Light theme fallback designed with high contrast and dark accents
private val PoseXLightColorScheme = lightColorScheme(
    primary = Color(0xFF7E22CE),
    onPrimary = Color.White,
    secondary = Color(0xFFE11D48),
    onSecondary = Color.White,
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = Color(0xFF0F0D15), // Preserve premium dark ambiance for camera experience
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF181523),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF242033),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

@Composable
fun PosexTheme(
    darkTheme: Boolean = true, // Default to photography dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PoseXDarkColorScheme else PoseXLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PosexTheme(darkTheme = true, content = content)
}
