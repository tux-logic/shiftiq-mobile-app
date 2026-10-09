package com.tuxlogic.shiftiq.mobile.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShiftIQPrimary,
    onPrimary = ShiftIQOnPrimary,
    primaryContainer = ShiftIQPrimaryContainer,
    onPrimaryContainer = ShiftIQOnPrimaryContainer,
    secondary = ShiftIQSecondary,
    onSecondary = ShiftIQOnSecondary,
    tertiary = ShiftIQTertiary,
    onTertiary = ShiftIQOnTertiary,
    background = ShiftIQBackgroundDark,
    surface = ShiftIQSurfaceDark,
    onBackground = ShiftIQOnSurfaceDark,
    onSurface = ShiftIQOnSurfaceDark,
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
    tertiary = ShiftIQTertiary,
    onTertiary = ShiftIQOnTertiary,
    background = ShiftIQBackgroundLight,
    surface = ShiftIQSurfaceLight,
    onBackground = ShiftIQOnSurfaceLight,
    onSurface = ShiftIQOnSurfaceLight,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShiftIQTypography,
        content = content
    )
}
