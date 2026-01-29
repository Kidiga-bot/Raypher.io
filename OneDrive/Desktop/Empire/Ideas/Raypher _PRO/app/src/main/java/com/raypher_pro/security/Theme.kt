package com.Raypher_Pro.security

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
        darkColorScheme(
                primary = Color(0xFF00D9FF), // Electric Cyan
                secondary = Color(0xFF10B981),
                tertiary = Color(0xFFEF4444),
                background = Color(0xFF0F172A), // Deep Charcoal
                surface = Color(0xFF1E293B),
                onPrimary = Color(0xFF0A0E1A), // Dark text on cyan
                onSecondary = Color.White,
                onTertiary = Color.White,
                onBackground = Color(0xFFE2E8F0),
                onSurface = Color(0xFFE2E8F0)
        )

@Composable
fun RaypherProTheme(content: @Composable () -> Unit) {
        MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
