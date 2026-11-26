package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQAccentSkyBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQOnSurfaceLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500

enum class ShiftIQLogoSize(
    val boxSize: Dp,
    val titleSize: TextUnit,
    val subtitleSize: TextUnit
) {
    Small(44.dp, 18.sp, 12.sp),
    Medium(72.dp, 26.sp, 14.sp),
    Large(96.dp, 32.sp, 16.sp)
}

/**
 * Official ShiftIQ Workshop logo — mirrors Flutter [ShiftIqLogo].
 */
@Composable
fun ShiftIQLogo(
    modifier: Modifier = Modifier,
    size: ShiftIQLogoSize = ShiftIQLogoSize.Medium,
    showText: Boolean = true,
    isDark: Boolean = false
) {
    val shape = RoundedCornerShape(size.boxSize * 0.25f)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size.boxSize)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = ShiftIQAccentSkyBlue.copy(alpha = 50f / 255f),
                    spotColor = Color.Black.copy(alpha = 40f / 255f)
                )
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ShiftIQPrimary, Navy900)
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = ShiftIQAccentSkyBlue.copy(alpha = 120f / 255f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WifiTethering,
                contentDescription = null,
                tint = ShiftIQAccentSkyBlue.copy(alpha = 60f / 255f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(size.boxSize * 0.35f)
            )
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size.boxSize * 0.55f)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = if (isDark) Color.White else ShiftIQPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    ) {
                        append("Shift")
                    }
                    withStyle(
                        SpanStyle(
                            color = ShiftIQAccentSkyBlue,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    ) {
                        append("IQ")
                    }
                    withStyle(
                        SpanStyle(
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else ShiftIQOnSurfaceLight,
                            fontWeight = FontWeight.Medium,
                            fontSize = size.titleSize * 0.82f
                        )
                    ) {
                        append(" Workshop")
                    }
                },
                fontSize = size.titleSize,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gestión Inteligente de Talleres Automotrices",
                color = if (isDark) Color.White.copy(alpha = 0.6f) else SoftSlate,
                fontSize = size.subtitleSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

private val SoftSlate = Slate500
