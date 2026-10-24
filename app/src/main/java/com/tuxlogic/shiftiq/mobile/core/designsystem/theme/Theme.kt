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

private val lightScheme = lightColorScheme(
    primary = primaryLightScheme,
    onPrimary = onPrimaryLightScheme,
    primaryContainer = primaryContainerLightScheme,
    onPrimaryContainer = onPrimaryContainerLightScheme,
    secondary = secondaryLightScheme,
    onSecondary = onSecondaryLightScheme,
    secondaryContainer = secondaryContainerLightScheme,
    onSecondaryContainer = onSecondaryContainerLightScheme,
    background = backgroundLightScheme,
    onBackground = onBackgroundLightScheme,
    surface = surfaceLightScheme,
    onSurface = onSurfaceLightScheme,
    surfaceVariant = surfaceVariantLightScheme,
    onSurfaceVariant = onSurfaceVariantLightScheme,
    outline = outlineLightScheme,
    outlineVariant = outlineVariantLightScheme
)

private val darkScheme = darkColorScheme(
    primary = primaryDarkScheme,
    onPrimary = onPrimaryDarkScheme,
    primaryContainer = primaryContainerDarkScheme,
    onPrimaryContainer = onPrimaryContainerDarkScheme,
    secondary = secondaryDarkScheme,
    onSecondary = onSecondaryDarkScheme,
    secondaryContainer = secondaryContainerDarkScheme,
    onSecondaryContainer = onSecondaryContainerDarkScheme,
    background = backgroundDarkScheme,
    onBackground = onBackgroundDarkScheme,
    surface = surfaceDarkScheme,
    onSurface = onSurfaceDarkScheme,
    surfaceVariant = surfaceVariantDarkScheme,
    onSurfaceVariant = onSurfaceVariantDarkScheme,
    outline = outlineDarkScheme,
    outlineVariant = outlineVariantDarkScheme
)

@Composable
fun ShiftIqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
