package com.tuxlogic.shiftiq.mobile.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * ShiftIQ brand tokens — aligned with Flutter `ShiftIQColors`
 * (shiftiq-mobile-app-flutter/lib/core/theme.dart).
 */
val ShiftIQPrimary = Color(0xFF1E3A8A)          // primaryBlue
val ShiftIQOnPrimary = Color(0xFFFFFFFF)
val ShiftIQPrimaryContainer = Color(0xFF60A5FA) // accentSkyBlue
val ShiftIQOnPrimaryContainer = Color(0xFF1E3A8A)

val ShiftIQSecondary = Color(0xFF0FB5BA)        // secondaryTeal
val ShiftIQOnSecondary = Color(0xFFFFFFFF)
val ShiftIQSecondaryContainer = Color(0xFFBCECE7)
val ShiftIQOnSecondaryContainer = Color(0xFF003735)

val ShiftIQTertiary = Color(0xFF3DBE6C)         // preventiveGreen
val ShiftIQOnTertiary = Color(0xFFFFFFFF)
val ShiftIQTertiaryContainer = Color(0xFFC7F5D7)
val ShiftIQOnTertiaryContainer = Color(0xFF0D3E1E)

val ShiftIQBackgroundLight = Color(0xFFF8FAFC)  // warmNeutral
val ShiftIQSurfaceLight = Color(0xFFF8FAFC)
val ShiftIQOnSurfaceLight = Color(0xFF1E293B)   // graphiteText
val ShiftIQOnSurfaceVariantLight = Color(0xFF475569)

val ShiftIQBackgroundDark = Color(0xFF0F172A)
val ShiftIQSurfaceDark = Color(0xFF0F172A)
val ShiftIQOnSurfaceDark = Color(0xFFF1F5F9)
val ShiftIQOnSurfaceVariantDark = Color(0xFF94A3B8)

val ShiftIQError = Color(0xFFDC2626)            // severityCrimson
val ShiftIQOnError = Color(0xFFFFFFFF)
val ShiftIQErrorContainer = Color(0xFFFFDAD6)
val ShiftIQOnErrorContainer = Color(0xFF93000A)
val ShiftIQWarning = Color(0xFFFBBF24)          // severityAmber
val ShiftIQSeverityOrange = Color(0xFFF97316)
val ShiftIQSuccess = Color(0xFF3DBE6C)          // preventiveGreen

val ShiftIQAccentSkyBlue = Color(0xFF60A5FA)
val ShiftIQBorderSlate = Color(0xFFCBD5E1)
val ShiftIQSurfaceVariant = Color(0xFFF1F5F9)
val ShiftIQOutlineVariant = Color(0xFFE2E8F0)

// Compatibility aliases used across feature screens
val Primary = ShiftIQPrimary
val PrimaryBlue = ShiftIQPrimary
val SeverityCrimson = ShiftIQError
val SeverityAmber = ShiftIQWarning
val TelematicsGreen = ShiftIQSuccess
val Navy900 = Color(0xFF0F172A)

val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)

// Specialized Telemetry & Action Colors
val CanBusCyan = Color(0xFF0284C7)
val ObdTeal = ShiftIQSecondary
val WhatsAppGreen = Color(0xFF25D366)
val WalBufferAmber = Color(0xFFD97706)

// Bottom navigation — same primary as Flutter brand
val NavActiveBlue = ShiftIQPrimary
val NavActivePill = Color(0xFFDBEAFE)
val NavInactiveSlate = Slate500
