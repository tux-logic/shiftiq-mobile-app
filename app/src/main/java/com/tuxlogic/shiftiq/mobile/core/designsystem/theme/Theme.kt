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

/**
 * Color schemes mirrored from Flutter `MaterialTheme.lightScheme()` / `darkScheme()`.
 */
private val DarkColorScheme = darkColorScheme(
    primary = ShiftIQAccentSkyBlue,
    onPrimary = ShiftIQPrimary,
    primaryContainer = ShiftIQPrimary,
    onPrimaryContainer = ShiftIQOnPrimary,
    secondary = ShiftIQSecondary,
    onSecondary = Color(0xFF003735),
    secondaryContainer = Color(0xFF004F4C),
    onSecondaryContainer = Color(0xFFBCECE7),
    tertiary = ShiftIQTertiary,
    onTertiary = Color(0xFF0D3E1E),
    tertiaryContainer = Color(0xFF1E5B33),
    onTertiaryContainer = Color(0xFFC7F5D7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = ShiftIQError,
    onErrorContainer = ShiftIQErrorContainer,
    background = ShiftIQBackgroundDark,
    surface = ShiftIQSurfaceDark,
    surfaceContainerLow = Color(0xFF141E33),
    surfaceContainer = Slate800,
    surfaceContainerHigh = Color(0xFF243248),
    surfaceContainerHighest = Slate700,
    onBackground = ShiftIQOnSurfaceDark,
    onSurface = ShiftIQOnSurfaceDark,
    onSurfaceVariant = ShiftIQOnSurfaceVariantDark,
    outline = Slate600,
    outlineVariant = Slate700,
    inverseSurface = ShiftIQSurfaceLight,
    inverseOnSurface = ShiftIQOnSurfaceLight,
    inversePrimary = ShiftIQPrimary,
    scrim = Color.Black
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
    tertiaryContainer = ShiftIQTertiaryContainer,
    onTertiaryContainer = ShiftIQOnTertiaryContainer,
    error = ShiftIQError,
    onError = ShiftIQOnError,
    errorContainer = ShiftIQErrorContainer,
    onErrorContainer = ShiftIQOnErrorContainer,
    background = ShiftIQBackgroundLight,
    surface = ShiftIQSurfaceLight,
    surfaceContainerLow = Slate50,
    surfaceContainer = Slate100,
    surfaceContainerHigh = Slate200,
    surfaceContainerHighest = Slate300,
    onBackground = ShiftIQOnSurfaceLight,
    onSurface = ShiftIQOnSurfaceLight,
    onSurfaceVariant = ShiftIQOnSurfaceVariantLight,
    outline = ShiftIQBorderSlate,
    outlineVariant = ShiftIQOutlineVariant,
    inverseSurface = Navy900,
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = ShiftIQAccentSkyBlue,
    scrim = Color.Black
)

@Composable
fun ShiftIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled by default so brand colors stay consistent with Flutter
    dynamicColor: Boolean = false,
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
