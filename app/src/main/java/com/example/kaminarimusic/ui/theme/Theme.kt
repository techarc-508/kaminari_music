package com.example.kaminarimusic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = KaminariPrimary,
    secondary = KaminariSecondary,
    tertiary = KaminariTertiary,
    background = KaminariDarkBackground,
    surface = KaminariDarkSurface,
    surfaceVariant = KaminariDarkSurfaceVariant,
    onBackground = KaminariPrimaryText,
    onSurface = KaminariPrimaryText,
    onSurfaceVariant = KaminariSecondaryText
)

private val LightColorScheme = lightColorScheme(
    primary = KaminariLightPrimary,
    secondary = KaminariLightSecondary,
    tertiary = KaminariTertiary,
    background = KaminariLightBackground,
    surface = KaminariLightSurface,
    surfaceVariant = KaminariLightSurfaceVariant,
    onBackground = KaminariLightOnBackground,
    onSurface = KaminariLightOnBackground,
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun KaminariMusicTheme(
    darkTheme: Boolean = true, // Default to immersive dark mode
    dynamicColor: Boolean = false, // Preserve bespoke Kaminari aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
