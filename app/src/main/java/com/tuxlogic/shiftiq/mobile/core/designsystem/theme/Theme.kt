package com.tuxlogic.shiftiq.mobile.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShiftIQPrimary,
    onPrimary = ShiftIQOnPrimary,
    primaryContainer = ShiftIQPrimaryContainer,
    onPrimaryContainer = ShiftIQOnPrimaryContainer,
    secondary = ShiftIQSecondary,
    onSecondary = ShiftIQOnSecondary,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFBFDBFE),
    tertiary = ShiftIQTertiary,
    onTertiary = ShiftIQOnTertiary,
    background = ShiftIQBackgroundDark,
    surface = Navy900,
    surfaceContainerLow = Color(0xFF141E33),
    surfaceContainer = Slate800,
    surfaceContainerHigh = Color(0xFF243248),
    surfaceContainerHighest = Slate700,
    onBackground = ShiftIQOnSurfaceDark,
    onSurface = Slate50,
    onSurfaceVariant = Slate400,
    outline = Slate600,
    outlineVariant = Color(0xFF1E3A8A),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF1E3A8A),
    error = ShiftIQError,
    onError = ShiftIQOnError
)

private val LightColorScheme = lightColorScheme(
    primary = ShiftIQPrimary,
    onPrimary = ShiftIQOnPrimary,
    primaryContainer = ShiftIQPrimaryContainer,
    onPrimaryContainer = ShiftIQOnPrimaryContainer,
    secondary = ShiftIQSecondary,
    onSecondary = ShiftIQOnSecondary,
    secondaryContainer = ShiftIQSecondaryContainer,
    onSecondaryContainer = ShiftIQOnSecondaryContainer,
    tertiary = ShiftIQTertiary,
    onTertiary = ShiftIQOnTertiary,
    background = ShiftIQBackgroundLight,
    surface = ShiftIQSurfaceLight,
    surfaceContainerLow = Slate50,
    surfaceContainer = Slate100,
    surfaceContainerHigh = Slate200,
    surfaceContainerHighest = Slate300,
    onBackground = ShiftIQOnSurfaceLight,
    onSurface = ShiftIQOnSurfaceLight,
    onSurfaceVariant = Slate500,
    outline = Slate400,
    outlineVariant = Slate200,
    inverseSurface = Navy900,
    inverseOnSurface = Color(0xFFFFFFFF),
    error = ShiftIQError,
    onError = ShiftIQOnError
)

@Composable
fun ShiftIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado por defecto para preservar identidad de marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ShiftIQTypography,
            content = content
        )
    }
}
