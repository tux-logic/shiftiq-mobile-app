package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun ShiftIQSkeletonBox(
    height: Dp,
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    borderRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeletonShimmer"
    )
    val brush = Brush.linearGradient(
        colors = listOf(Slate200, Slate50, SoftSlateMid, Slate200),
        start = Offset(shimmer * 400f - 200f, 0f),
        end = Offset(shimmer * 400f + 200f, 200f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(borderRadius))
            .background(brush)
    )
}

private val SoftSlateMid = Color(0xFFE8EEF5)

@Composable
fun ShiftIQSkeletonDetail(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.medium)
    ) {
        ShiftIQSkeletonBox(height = 120.dp, borderRadius = 20.dp)
        Spacer(modifier = Modifier.height(spacing.medium))
        ShiftIQSkeletonBox(height = 18.dp, widthFraction = 0.55f)
        Spacer(modifier = Modifier.height(spacing.small))
        ShiftIQSkeletonBox(height = 14.dp, widthFraction = 0.85f)
        Spacer(modifier = Modifier.height(spacing.small))
        ShiftIQSkeletonBox(height = 14.dp, widthFraction = 0.7f)
        Spacer(modifier = Modifier.height(spacing.large))
        ShiftIQSkeletonBox(height = 88.dp, borderRadius = 16.dp)
        Spacer(modifier = Modifier.height(spacing.medium))
        ShiftIQSkeletonBox(height = 88.dp, borderRadius = 16.dp)
    }
}
