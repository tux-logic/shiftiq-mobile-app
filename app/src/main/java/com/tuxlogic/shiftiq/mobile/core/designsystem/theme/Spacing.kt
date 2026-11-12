package com.tuxlogic.shiftiq.mobile.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard 8-point Material 3 Spacing System for ShiftIQ.
 * Centralizes all layout spacing, paddings, gaps, and structural dimensions
 * to enforce zero hardcoded .dp literals across feature modules.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val micro: Dp = 2.dp,
    val extraSmall: Dp = 4.dp,
    val compact: Dp = 6.dp,
    val small: Dp = 8.dp,
    val smallMedium: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val extraExtraLarge: Dp = 48.dp,

    // Component-level tokens
    val divider: Dp = 1.dp,
    val indicatorHeight: Dp = 6.dp,
    val iconSmall: Dp = 14.dp,
    val iconMedium: Dp = 16.dp,
    val iconLarge: Dp = 20.dp,
    val iconExtraLarge: Dp = 24.dp,
    val buttonHeight: Dp = 44.dp,

    // Corner Radii Tokens
    val radiusExtraSmall: Dp = 4.dp,
    val radiusSmall: Dp = 8.dp,
    val radiusMedium: Dp = 12.dp,
    val radiusLarge: Dp = 16.dp,
    val radiusExtraLarge: Dp = 20.dp,
    val radiusFull: Dp = 999.dp,

    // Layout & Component Dimensions
    val chipHeight: Dp = 32.dp,
    val badgeHeight: Dp = 24.dp,
    val topBarHeight: Dp = 64.dp,
    val bottomNavHeight: Dp = 64.dp,
    val navPillWidth: Dp = 40.dp,
    val navPillHeight: Dp = 24.dp,
    val navItemWidth: Dp = 64.dp,
    val avatarSmall: Dp = 32.dp,
    val avatarCompact: Dp = 36.dp,
    val avatarMedium: Dp = 44.dp,
    val avatarLarge: Dp = 56.dp,
    val drawerWidth: Dp = 312.dp,
    val gaugeSmall: Dp = 112.dp,
    val gaugeMedium: Dp = 160.dp,
    val gaugeLarge: Dp = 180.dp,
    val mapHeight: Dp = 176.dp,
    val cardMinHeight: Dp = 64.dp,
    val radarSize: Dp = 96.dp,
    val stepperNodeSize: Dp = 32.dp,
    val slotCardWidth: Dp = 100.dp,
    val slotCardHeight: Dp = 64.dp,
    val dayCardWidth: Dp = 68.dp,
    val dayCardHeight: Dp = 76.dp,
    val vehicleCardHeight: Dp = 120.dp,
    val dropzoneHeight: Dp = 120.dp,
    val thumbSize: Dp = 48.dp,
    val timelineDotSize: Dp = 12.dp,
    val elevationSmall: Dp = 2.dp,
    val elevationMedium: Dp = 4.dp,
    val strokeThin: Dp = 1.dp,
    val strokeMedium: Dp = 2.dp,
    val strokeThick: Dp = 3.dp,
    val strokeGauge: Dp = 12.dp,
    val strokeGaugeSmall: Dp = 8.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
